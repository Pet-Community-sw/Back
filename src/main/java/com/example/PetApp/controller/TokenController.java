package com.example.PetApp.controller;

import com.example.PetApp.service.member.TokenServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenServiceImp tokenServiceImp;

    @PostMapping("/token")
    public ResponseEntity<?> accessToken(@RequestHeader("Authorization") String accessToken) {
        return tokenServiceImp.accessToken(accessToken);
    }
}
