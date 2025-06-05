package com.example.PetApp.service.review;

import com.example.PetApp.dto.review.*;
import org.springframework.stereotype.Service;

@Service
public interface ReviewService {
    CreateReviewResponseDto createReview(CreateReviewDto createReviewDto, String email);

    GetReviewResponseDto getReview(Long reviewId, String email);

    void updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email);

    void deleteReview(Long reviewId, String email);

    GetReviewListResponseDto getReviewListByMember(Long memberId, String email);

    GetReviewListResponseDto getReviewListByProfile(Long profileId, String email);
}
