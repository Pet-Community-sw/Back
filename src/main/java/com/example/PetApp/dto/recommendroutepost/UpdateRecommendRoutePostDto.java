package com.example.PetApp.dto.recommendroutepost;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//장소바꾸지못함 바꾸려면 삭제했다가 다시 설정해야됨.
public class UpdateRecommendRoutePostDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}

