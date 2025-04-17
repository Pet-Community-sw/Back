package com.example.PetApp.controller;

import com.example.PetApp.service.user.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<?> accessToken(@RequestHeader("Authorization") String accessToken) {
        return tokenService.accessToken(accessToken);
    }
}
