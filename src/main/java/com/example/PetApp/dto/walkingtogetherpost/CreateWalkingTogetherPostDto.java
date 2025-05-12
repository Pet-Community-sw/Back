package com.example.PetApp.dto.walkingtogetherpost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter//추후에 날짜와 시간으로 값 내려받기
public class CreateWalkingTogetherPostDto {

    private Long recommendRoutePostId;

    private String content;

    private int limitCount;
}
