package com.example.PetApp.dto.profile;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenToProfileIdResponseDto {
    private Long profileId;

    private String accessToken;

}
