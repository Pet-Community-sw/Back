package com.example.PetApp.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetReviewResponseDto {

    private Long reviewId;

    @Setter
    private Long userId;

    @Setter
    private String userName;

    @Setter
    private String userImageUrl;

    private String title;

    private String content;

    private Integer rating;

    private LocalDateTime reviewTime;

    @Setter
    private boolean isOwner;

}
