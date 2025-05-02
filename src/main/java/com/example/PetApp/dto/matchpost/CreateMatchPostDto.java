package com.example.PetApp.dto.matchpost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMatchPostDto {
    private Long profileId;

    private Double longitude;

    private Double latitude;

    private String locationName;

    private String content;

    private int limitCount;
}
