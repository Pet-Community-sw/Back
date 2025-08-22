package com.example.PetApp.dto.member;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String name;

    private String accessToken;
}
