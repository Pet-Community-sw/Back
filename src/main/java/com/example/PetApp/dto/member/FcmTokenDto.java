package com.example.PetApp.dto.member;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@NotBlank
public class FcmTokenDto {

    private Long memberId;

    private String fcmToken;
}
