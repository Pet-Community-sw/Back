package com.example.PetApp.dto.member;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenDto {
    private Long memberId;
    private String fcmToken;
}
