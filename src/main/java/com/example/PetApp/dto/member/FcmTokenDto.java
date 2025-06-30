package com.example.PetApp.dto.member;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenDto {

    @NotNull(message = "사용자id는 필수입니다.")
    private Long memberId;

    @NotBlank(message = "토큰은 필수입니다.")
    private String fcmToken;
}
