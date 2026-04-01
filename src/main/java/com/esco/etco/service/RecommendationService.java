package com.esco.etco.service;

import java.util.List;
import java.util.Map;

public interface RecommendationService {
    public List<Long> getRecommendedEventIds(long currentUserId);

    double calculateCosineSimilarity(Map<String, Integer> v1, Map<String, Integer> v2);
}
