package com.example.PetApp.dto.delegateWalkpost;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UpdateDelegateWalkPostDto {

    private String title;

    private String content;

    private Long price;

    private Integer allowedRedisMeters;

    private boolean requireProfile;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;

}
