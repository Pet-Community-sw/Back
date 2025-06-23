package com.example.PetApp.service.schedule;

import com.example.PetApp.dto.schedule.GetSchedulesResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleService {

    List<GetSchedulesResponseDto> getSchedules(String start, String finish, Long profileId);
}
