package com.example.PetApp.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetReviewListResponseDto {

    private Long userId;

    private String userName;

    private String userImageUrl;

    private Double averageRating;

    private int reviewCount;

    List<GetReviewList> reviewList;
}
