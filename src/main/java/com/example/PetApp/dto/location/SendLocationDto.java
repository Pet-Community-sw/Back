package com.example.PetApp.dto.location;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendLocationDto {
    Double locationLongitude;
    Double locationLatitude;
    Double walkerLongitude;
    Double walkerLatitude;
}
