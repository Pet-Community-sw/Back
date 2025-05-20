package com.example.PetApp.controller;

import com.example.PetApp.dto.review.CreateReviewDto;
import com.example.PetApp.dto.review.UpdateReviewDto;
import com.example.PetApp.service.review.ReviewService;
import com.example.PetApp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateReviewDto createReviewDto, Authentication authentication) {
        return reviewService.createReview(createReviewDto, authUtil.getEmail(authentication));
    }

    @GetMapping("/{memberId}/list/member")
    public ResponseEntity<?> getReviewListByMember(@PathVariable Long memberId, Authentication authentication) {
        return reviewService.getReviewListByMember(memberId, authUtil.getEmail(authentication));
    }

    @GetMapping("/{profileId}/list/profile")
    public ResponseEntity<?> getReviewListByProfile(@PathVariable Long profileId, Authentication authentication) {
        return reviewService.getReviewListByProfile(profileId, authUtil.getEmail(authentication));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable Long reviewId, Authentication authentication) {
        return reviewService.getReview(reviewId, authUtil.getEmail(authentication));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestBody UpdateReviewDto updateReviewDto,
                                          Authentication authentication) {
        return reviewService.updateReview(reviewId, updateReviewDto, authUtil.getEmail(authentication));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        return reviewService.deleteReview(reviewId, authUtil.getEmail(authentication));
    }
}
