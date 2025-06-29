package com.example.PetApp.dto.recommendroutepost;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
public class CreateRecommendRoutePostDto {
    private String title;

    private String content;

    private String locationName;

    private Double locationLongitude;

    private Double locationLatitude;

}
