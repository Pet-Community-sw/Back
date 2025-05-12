package com.example.PetApp.dto.recommendroutepost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateRecommendRoutePostDto {
    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private Double locationLongitude;

    @NotEmpty
    private Double locationLatitude;

}
