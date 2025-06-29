package com.example.PetApp.dto.walkrecord;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class LocationMessage {
    private Long walkRecordId;

    private Double longitude;

    private Double latitude;
}
