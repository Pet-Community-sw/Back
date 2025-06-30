package com.example.PetApp.controller;

import com.example.PetApp.dto.MessageResponse;
import com.example.PetApp.dto.review.*;
import com.example.PetApp.service.review.ReviewService;
import com.example.PetApp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public CreateReviewResponseDto createReview(@RequestBody @Valid CreateReviewDto createReviewDto, Authentication authentication) {
        return reviewService.createReview(createReviewDto, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{memberId}/list/member")
    public GetReviewListResponseDto getReviewListByMember(@PathVariable Long memberId, Authentication authentication) {
        return reviewService.getReviewListByMember(memberId, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{profileId}/list/profile")
    public GetReviewListResponseDto getReviewListByProfile(@PathVariable Long profileId, Authentication authentication) {
        return reviewService.getReviewListByProfile(profileId, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{reviewId}")
    public GetReviewResponseDto getReview(@PathVariable Long reviewId, Authentication authentication) {
        return reviewService.getReview(reviewId, AuthUtil.getEmail(authentication));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> updateReview(@PathVariable Long reviewId, @RequestBody @Valid UpdateReviewDto updateReviewDto, Authentication authentication) {
        reviewService.updateReview(reviewId, updateReviewDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        reviewService.deleteReview(reviewId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

}
