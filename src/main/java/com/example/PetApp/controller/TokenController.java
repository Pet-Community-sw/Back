package com.example.PetApp.controller;

import com.example.PetApp.dto.member.AccessTokenResponseDto;
import com.example.PetApp.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public AccessTokenResponseDto reissueAccessToken(@RequestHeader("Authorization") String accessToken, @CookieValue("refreshToken") String refreshToken) {
        return tokenService.reissueAccessToken(accessToken, refreshToken);
    }
}
