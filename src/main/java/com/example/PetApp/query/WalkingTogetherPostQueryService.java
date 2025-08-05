package com.example.PetApp.query;

import com.example.PetApp.domain.WalkingTogetherPost;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.WalkingTogetherPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalkingTogetherPostQueryService {
    private final WalkingTogetherPostRepository walkingTogetherPostRepository;

    public WalkingTogetherPost findByWalkingTogetherPost(Long walkingTogetherPostId) {
        return walkingTogetherPostRepository.findById(walkingTogetherPostId).orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }
}
