package com.example.PetApp.dto.review;

import lombok.*;

import javax.validation.constraints.NotBlank;

import static com.example.PetApp.domain.Review.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
public class CreateReviewDto {

    private Long walkRecordId;

    private String title;

    private String content;

    private Integer rating;

    private ReviewType reviewType;
}
