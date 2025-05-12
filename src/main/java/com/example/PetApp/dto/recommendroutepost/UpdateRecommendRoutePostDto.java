package com.example.PetApp.dto.recommendroutepost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
//장소바꾸지못함 바꾸려면 삭제했다가 다시 설정해야됨.
public class UpdateRecommendRoutePostDto {

    private String title;

    private String content;
}
