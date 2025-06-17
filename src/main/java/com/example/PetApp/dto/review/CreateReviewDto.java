package com.example.PetApp.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.example.PetApp.domain.Review.*;

@Getter
@Setter
@Builder
public class CreateReviewDto {

    private Long walkRecordId;

    private String title;

    private String content;

    private Integer rating;

    private ReviewType reviewType;
}
