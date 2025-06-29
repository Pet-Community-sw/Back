package com.example.PetApp.dto.member;


import lombok.*;

@Getter
@Builder
public class LoginResponseDto {
    private String name;

    private String accessToken;
}
