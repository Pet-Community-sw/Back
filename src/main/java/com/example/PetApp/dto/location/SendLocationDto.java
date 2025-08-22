package com.example.PetApp.dto.location;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendLocationDto {
    Double locationLongitude;
    Double locationLatitude;
    Double walkerLongitude;
    Double walkerLatitude;
}
