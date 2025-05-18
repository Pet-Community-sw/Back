package com.example.PetApp.service.review;

import com.example.PetApp.dto.review.CreateReviewDto;
import com.example.PetApp.dto.review.UpdateReviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ReviewService {
    ResponseEntity<?> createReview(CreateReviewDto createReviewDto, String email);

    ResponseEntity<?> getReview(Long reviewId, String email);

    ResponseEntity<?> updateReview(Long reviewId, UpdateReviewDto updateReviewDto, String email);

    ResponseEntity<?> deleteReview(Long reviewId, String email);

    ResponseEntity<?> getReviewListByMember(Long memberId, String email);

    ResponseEntity<?> getReviewListByProfile(Long profileId, String email);
}
