package com.example.PetApp.dto.profile;

import lombok.*;

@Getter
@Builder
public class AccessTokenByProfileIdResponseDto {
    private Long profileId;

    private String accessToken;

}
