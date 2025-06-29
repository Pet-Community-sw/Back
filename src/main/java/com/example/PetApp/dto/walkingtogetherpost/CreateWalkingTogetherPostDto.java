package com.example.PetApp.dto.walkingtogetherpost;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter//추후에 날짜와 시간으로 값 내려받기
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalkingTogetherPostDto {

    @NotBlank
    private Long recommendRoutePostId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;

    @NotNull
    private int limitCount;
}
