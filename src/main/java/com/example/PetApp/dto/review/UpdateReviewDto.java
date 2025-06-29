package com.example.PetApp.dto.review;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
public class UpdateReviewDto {

    private String title;

    private String content;

    private Integer rating;
}
