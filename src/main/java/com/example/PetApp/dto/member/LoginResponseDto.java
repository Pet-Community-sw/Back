package com.example.PetApp.dto.member;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private String name;
    private String accessToken;
}
