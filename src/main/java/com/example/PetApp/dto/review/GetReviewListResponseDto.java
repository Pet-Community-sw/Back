package com.example.PetApp.dto.review;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetReviewListResponseDto {

    private Long userId;

    private String userName;

    private String userImageUrl;

    private Double averageRating;

    private int reviewCount;

    List<GetReviewList> reviewList;
}
