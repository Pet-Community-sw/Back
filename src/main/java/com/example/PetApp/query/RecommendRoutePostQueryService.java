package com.example.PetApp.query;

import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendRoutePostQueryService {
    private final RecommendRoutePostRepository recommendRoutePostRepository;

    public RecommendRoutePost findByRecommendRoutePost(Long recommendRoutePostId) {
        return recommendRoutePostRepository.findById(recommendRoutePostId).orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
    }
}
