package com.example.PetApp.dto.profile;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProfileResponseDto {

    private Long profileId;

    private String accessToken;
}
