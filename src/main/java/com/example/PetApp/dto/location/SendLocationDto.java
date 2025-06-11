package com.example.PetApp.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SendLocationDto {
    Double locationLongitude;
    Double locationLatitude;
    Double walkerLongitude;
    Double walkerLatitude;
}
