package com.example.PetApp.dto.schedule;


import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetSchedulesResponseDto {

    private Long memberId;

    private LocalDateTime scheduleDate;

    private ScheduleType scheduleType;

}
