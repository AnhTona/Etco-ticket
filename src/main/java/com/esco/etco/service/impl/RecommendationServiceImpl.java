package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.UserTicket;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.repository.UserTicketRepository;
import com.esco.etco.util.constant.UserTicketStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.esco.etco.service.RecommendationService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserTicketRepository userTicketRepository;
    private final EventRepository eventRepository;

    @Override
    public List<Long> getRecommendedEventIds(long currentUserId) {
        // Lấy tất cả dữ liệu vé
        List<UserTicket> allTickets = userTicketRepository.findAll();

        // Lọc ra các vé thuộc sự kiện ĐÃ KẾT THÚC (Current_Time > event.end_time)
        // và vé không bị hủy (để tính lịch sử chính xác)
        Instant now = Instant.now();
        List<UserTicket> validHistoryTickets = allTickets.stream()
                .filter(ut -> ut.getEvent() != null && ut.getEvent().getEndTime() != null)
                .filter(ut -> ut.getEvent().getEndTime().isBefore(now))
                .filter(ut -> ut.getStatus() != null && ut.getStatus() != UserTicketStatusEnum.CANCELLED)
                .toList();

        if (validHistoryTickets.isEmpty()) return Collections.emptyList();

        // Xây dựng user profile (vector) dựa trên:
        // 1. Tên nghệ sĩ
        // 2. Thể loại sự kiện (genre)
        // Cấu trúc: Map<UserId, Map<FeatureName, Score>>
        Map<Long, Map<String, Integer>> userProfiles = new HashMap<>();

        for (UserTicket ticket : validHistoryTickets) {
            long userId = ticket.getUser().getId();
            Event event = ticket.getEvent();

            userProfiles.putIfAbsent(userId, new HashMap<>());
            Map<String, Integer> profile = userProfiles.get(userId);

            // Thêm điểm cho thể loại sự kiện
            if (event.getGenre() != null && event.getGenre().getName() != null) {
                String genreFeature = "GENRE_" + event.getGenre().getName().toLowerCase();
                profile.put(genreFeature, profile.getOrDefault(genreFeature, 0) + 1);
            }

            // Thêm điểm cho từng nghệ sĩ tham gia sự kiện
            if (event.getArtists() != null) {
                for (String artist : event.getArtists()) {
                    String artistFeature = "ARTIST_" + artist.toLowerCase();
                    profile.put(artistFeature, profile.getOrDefault(artistFeature, 0) + 1);
                }
            }
        }

        Map<String, Integer> currentUserProfile = userProfiles.getOrDefault(currentUserId, Collections.emptyMap());
        // Nếu người dùng hiện tại chưa có lịch sử, không thể gợi ý qua RAG / Collaborative
        if (currentUserProfile.isEmpty()) return Collections.emptyList();

        // Tính độ tương đồng Cosine giữa currentUserId và những người dùng khác
        Map<Long, Double> userSimilarities = new HashMap<>();
        for (Map.Entry<Long, Map<String, Integer>> entry : userProfiles.entrySet()) {
            long otherUserId = entry.getKey();
            if (otherUserId == currentUserId) continue;

            double similarity = calculateCosineSimilarity(currentUserProfile, entry.getValue());
            if (similarity > 0) {
                userSimilarities.put(otherUserId, similarity);
            }
        }

        // Lấy danh sách các sự kiện SẮP/ĐANG DIỄN RA
        List<Event> upcomingEvents = eventRepository.findAll().stream()
                .filter(e -> e.getEndTime() != null && e.getEndTime().isAfter(now))
                .filter(e -> e.isActive() && e.isPublished())
                .toList();

        // Tính điểm cho mỗi sự kiện sắp diễn ra
        // Công thức lai (Hybrid): Tương đồng với người dùng khác * Điểm trùng khớp sở thích của sự kiện
        Map<Long, Double> eventScores = new HashMap<>();

        for (Event upcomingEvent : upcomingEvents) {
            double baseEventScore = calculateEventScore(upcomingEvent, currentUserProfile);

            if (baseEventScore > 0) {
                // Nếu có tương đồng với các user khác, nhân thêm trọng số (Collaborative weight)
                double collaborativeWeight = 1.0;
                for (Map.Entry<Long, Double> sim : userSimilarities.entrySet()) {
                    long similarUserId = sim.getKey();
                    double similarityScore = sim.getValue();

                    // Kiểm tra xem người dùng tương đồng đã từng đi sự kiện tương tự chưa
                    Map<String, Integer> similarUserProfile = userProfiles.get(similarUserId);
                    double similarUserInterest = calculateEventScore(upcomingEvent, similarUserProfile);
                    
                    if (similarUserInterest > 0) {
                        collaborativeWeight += (similarityScore * similarUserInterest);
                    }
                }
                eventScores.put(upcomingEvent.getId(), baseEventScore * collaborativeWeight);
            }
        }

        return eventScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10) // Gợi ý tối đa 10 sự kiện
                .map(Map.Entry::getKey)
                .toList();
    }
    
    // Tính điểm sở thích cho một sự kiện (Content-based)
    private double calculateEventScore(Event event, Map<String, Integer> userProfile) {
        double score = 0.0;
        if (event.getGenre() != null && event.getGenre().getName() != null) {
            String gFeat = "GENRE_" + event.getGenre().getName().toLowerCase();
            score += userProfile.getOrDefault(gFeat, 0);
        }
        if (event.getArtists() != null) {
            for (String artist : event.getArtists()) {
                String aFeat = "ARTIST_" + artist.toLowerCase();
                score += userProfile.getOrDefault(aFeat, 0) * 2.0; // Ưu tiên nghệ sĩ (trọng số 2)
            }
        }
        return score;
    }

    @Override
    public double calculateCosineSimilarity(Map<String, Integer> v1, Map<String, Integer> v2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String feature : v1.keySet()) {
            dotProduct += v1.get(feature) * v2.getOrDefault(feature, 0);
            normA += Math.pow(v1.get(feature), 2);
        }
        for (Integer score : v2.values()) {
            normB += Math.pow(score, 2);
        }

        return (normA == 0 || normB == 0) ? 0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
