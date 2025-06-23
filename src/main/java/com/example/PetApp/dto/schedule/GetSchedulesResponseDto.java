package com.example.PetApp.dto.schedule;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class GetSchedulesResponseDto {

    private Long memberId;

    private LocalDateTime scheduleDate;

    private ScheduleType scheduleType;

}
