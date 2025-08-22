package com.example.PetApp.dto.walkingtogetherpost;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalkingTogetherPostDto {

    @NotNull(message = "산책길추천id는 필수입니다.")
    private Long recommendRoutePostId;

    @NotNull(message = "함께 산책해요 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;

    @NotNull(message = "제한인원은 필수입니다.")
    private int limitCount;
}
