package com.example.PetApp.dto.review;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewDto {

    @NotBlank(message = "리뷰 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    private String content;

    @NotNull(message = "리뷰 별점은 필수입니다.")
    private Integer rating;
}
