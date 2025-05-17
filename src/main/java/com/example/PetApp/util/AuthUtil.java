package com.example.PetApp.util;

import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    public Long getProfileId(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return jwtAuthenticationToken.getProfileId();
    }
}
