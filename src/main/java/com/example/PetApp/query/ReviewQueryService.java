package com.example.PetApp.query;

import com.example.PetApp.domain.Review;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public Review findByReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException("해당 리뷰는 없습니다."));
    }
}
