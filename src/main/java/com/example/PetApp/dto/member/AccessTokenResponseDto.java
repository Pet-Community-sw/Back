package com.example.PetApp.dto.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponseDto {
    private String newAccessToken;
}
