package com.example.PetApp.dto.walkrecord;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetWalkRecordResponseDto {

    private Long walkRecordId;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private String walkTime;

    private Double walkDistance;

    private List<String> pathPoints;

}
