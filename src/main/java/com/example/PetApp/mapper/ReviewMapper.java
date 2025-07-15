package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.Review;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.domain.embedded.PostContent;
import com.example.PetApp.dto.review.CreateReviewDto;
import com.example.PetApp.dto.review.GetReviewList;
import com.example.PetApp.dto.review.GetReviewListResponseDto;
import com.example.PetApp.dto.review.GetReviewResponseDto;

import java.util.List;
import java.util.stream.Collectors;


public class ReviewMapper {
    public static Review toEntity(WalkRecord walkRecord, CreateReviewDto createReviewDto) {
        return Review.builder()
                .member(walkRecord.getMember())
                .profile(walkRecord.getDelegateWalkPost().getProfile())
                .walkRecord(walkRecord)
                .postContent(new PostContent(createReviewDto.getTitle(), createReviewDto.getContent()))
                .rating(createReviewDto.getRating())
                .reviewType(createReviewDto.getReviewType())
                .build();
    }

    public static GetReviewResponseDto toGetReviewResponseDto(Review review, Member member) {
        GetReviewResponseDto getReviewResponseDto = GetReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .title(review.getPostContent().getTitle())
                .content(review.getPostContent().getContent())
                .rating(review.getRating())
                .reviewTime(review.getCreatedAt())
                .build();
        if (review.getReviewType() == Review.ReviewType.PROFILE_TO_MEMBER) {
            getReviewResponseDto.setUserId(review.getProfile().getProfileId());
            getReviewResponseDto.setUserName(review.getProfile().getPetName());
            getReviewResponseDto.setUserImageUrl(review.getProfile().getPetImageUrl());
            getReviewResponseDto.setOwner(member.equals(review.getProfile().getMember()));
        } else {
            getReviewResponseDto.setUserId(review.getMember().getMemberId());
            getReviewResponseDto.setUserName(review.getMember().getName());
            getReviewResponseDto.setUserImageUrl(review.getMember().getMemberImageUrl());
            getReviewResponseDto.setOwner(member.equals(review.getMember()));
        }
        return getReviewResponseDto;
    }

    public static GetReviewListResponseDto toGetReviewListResponseDto(List<Review> reviews, Long userId, String userName, String userImageUrl, List<GetReviewList> getReviewLists) {
        return GetReviewListResponseDto.builder()
                .userId(userId)
                .userName(userName)
                .userImageUrl(userImageUrl)
                .averageRating(reviews.stream().mapToInt(Review::getRating).average().orElse(0.0))
                .reviewCount(reviews.size())
                .reviewList(getReviewLists)
                .build();

    }

    public static List<GetReviewList> toGetReviewList(List<Review> reviews, Member member) {
        return reviews.stream()
                .map(review -> GetReviewList.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getProfile().getProfileId())
                        .userName(review.getProfile().getPetName())
                        .userImageUrl(review.getProfile().getPetImageUrl())
                        .title(review.getPostContent().getTitle())
                        .rating(review.getRating())
                        .reviewTime(review.getCreatedAt())
                        .isOwner(review.getMember().equals(member))
                        .build()
                ).collect(Collectors.toList());
    }
}
