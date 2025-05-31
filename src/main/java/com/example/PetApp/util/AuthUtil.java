package com.example.PetApp.util;

import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

public class AuthUtil {

    public static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    public static Long getProfileId(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return jwtAuthenticationToken.getProfileId();
    }
}
