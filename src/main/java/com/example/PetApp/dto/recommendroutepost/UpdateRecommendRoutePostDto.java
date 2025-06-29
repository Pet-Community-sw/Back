package com.example.PetApp.dto.recommendroutepost;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
//장소바꾸지못함 바꾸려면 삭제했다가 다시 설정해야됨.
public class UpdateRecommendRoutePostDto {

    private String title;

    private String content;
}

