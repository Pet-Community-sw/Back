package com.example.PetApp.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GetReviewResponseDto {

    private Long reviewId;

    private Long userId;

    private String userName;

    private String userImageUrl;

    private String title;

    private String content;

    private Integer rating;

    private LocalDateTime reviewTime;

    private boolean isOwner;

}
