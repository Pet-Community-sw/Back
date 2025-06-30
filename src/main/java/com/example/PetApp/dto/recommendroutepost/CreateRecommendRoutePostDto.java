package com.example.PetApp.dto.recommendroutepost;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecommendRoutePostDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "장소이름은 필수입니다.")
    private String locationName;

    @NotNull(message = "위치경도는 필수입니다.")
    private Double locationLongitude;

    @NotNull(message = "위치위도는 필수입니다.")
    private Double locationLatitude;

}
