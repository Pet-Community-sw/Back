package com.example.PetApp.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateReviewDto {

    private String title;

    private String content;

    private Integer rating;
}
