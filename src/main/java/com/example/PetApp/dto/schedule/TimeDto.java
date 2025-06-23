package com.example.PetApp.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TimeDto {

    private LocalDateTime start;
    private LocalDateTime end;
}
