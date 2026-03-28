package com.esco.etco.controller.admin;

import com.esco.etco.entity.User;
import com.esco.etco.entity.response.event.ResEventDTO;
import com.esco.etco.repository.UserRepository;
import com.esco.etco.service.EventService;
import com.esco.etco.service.RecommendationService;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final EventService eventService;
    private final UserRepository userRepository;

    @GetMapping("/events/recommendations")
    @ApiMessage("Lấy danh sách sự kiện gợi ý cho bạn")
    public ResponseEntity<List<ResEventDTO>> getRecommendations() throws IdInvalidException {
        // Lấy email người dùng hiện tại từ SecurityContext
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Bạn cần đăng nhập để sử dụng tính năng này"));

        // Lấy thông tin User
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("Không tìm thấy thông tin người dùng");
        }
        long currentUserId = user.getId();

        // Gọi Service tính toán Collaborative Filtering
        List<Long> recommendedIds = recommendationService.getRecommendedEventIds(currentUserId);

        List<ResEventDTO> recommendations;

        // KIỂM TRA COLD START Ở ĐÂY
        if (recommendedIds == null || recommendedIds.isEmpty()) {
            // Nếu không có gợi ý cá nhân hóa -> Lấy sự kiện mới nhất
            recommendations = eventService.getFallbackRecommendations();
        } else {
            // Nếu có -> Lấy chi tiết các sự kiện đó
            recommendations = eventService.getRecommendedEvents(recommendedIds);
        }

        return ResponseEntity.ok(recommendations);
    }
}