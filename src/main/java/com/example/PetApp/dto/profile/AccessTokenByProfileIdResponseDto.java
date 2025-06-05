package com.example.PetApp.dto.profile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenByProfileIdResponseDto {
    private Long profileId;

    private String accessToken;

}
