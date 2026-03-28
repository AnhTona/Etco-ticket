package com.esco.etco.service.impl;

import com.esco.etco.entity.UserTicket;
import com.esco.etco.repository.UserTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.esco.etco.service.RecommendationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserTicketRepository userTicketRepository;

    @Override
    public List<Long> getRecommendedEventIds(long currentUserId) {
        // Lấy tất cả dữ liệu vé để xây dựng ma trận (trong thực tế nên giới hạn thời gian)
        List<UserTicket> allTickets = userTicketRepository.findAll();

        // Chuyển đổi dữ liệu thành Map<UserId, Map<EventId, Integer>> (số lượng vé mỗi sự kiện)
        Map<Long, Map<Long, Integer>> userEventMatrix = allTickets.stream()
                .collect(Collectors.groupingBy(
                        ut -> ut.getUser().getId(),
                        Collectors.groupingBy(ut -> ut.getEvent().getId(), Collectors.summingInt(e -> 1))
                ));

        Map<Long, Integer> currentUserVector = userEventMatrix.getOrDefault(currentUserId, Collections.emptyMap());
        if (currentUserVector.isEmpty()) return Collections.emptyList();

        // Tính độ tương đồng Cosine giữa currentUserId và những người dùng khác
        Map<Long, Double> userSimilarities = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Integer>> entry : userEventMatrix.entrySet()) {
            long otherUserId = entry.getKey();
            if (otherUserId == currentUserId) continue;

            double similarity = calculateCosineSimilarity(currentUserVector, entry.getValue());
            if (similarity > 0) {
                userSimilarities.put(otherUserId, similarity);
            }
        }

        // Lấy Top người dùng tương đồng nhất và gợi ý Event mà user hiện tại chưa mua
        return userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(5) // Lấy top 5 người dùng giống nhất
                .flatMap(entry -> userEventMatrix.get(entry.getKey()).keySet().stream())
                .filter(eventId -> !currentUserVector.containsKey(eventId))
                .distinct()
                .limit(10) // Gợi ý tối đa 10 sự kiện
                .collect(Collectors.toList());
    }

    @Override
    public double calculateCosineSimilarity(Map<Long, Integer> v1, Map<Long, Integer> v2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (Long id : v1.keySet()) {
            dotProduct += v1.get(id) * v2.getOrDefault(id, 0);
            normA += Math.pow(v1.get(id), 2);
        }
        for (Integer score : v2.values()) {
            normB += Math.pow(score, 2);
        }

        return (normA == 0 || normB == 0) ? 0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}