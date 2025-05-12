package com.example.PetApp.dto.walkingtogetherpost;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter//추후에 날짜와 시간으로 값 내려받기
public class CreateWalkingTogetherPostDto {

    private Long recommendRoutePostId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;

    private int limitCount;
}
