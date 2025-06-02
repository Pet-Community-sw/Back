package com.example.PetApp.service.token;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.AccessTokenResponseDto;
import com.example.PetApp.dto.member.LoginResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    void deleteRefreshToken(String email);

    LoginResponseDto save(Member member);

    AccessTokenResponseDto accessToken(String accessToken);
}
