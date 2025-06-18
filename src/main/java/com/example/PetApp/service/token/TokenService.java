package com.example.PetApp.service.token;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.AccessTokenResponseDto;
import com.example.PetApp.dto.member.LoginResponseDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface TokenService {
    void deleteRefreshToken(String email);

    LoginResponseDto save(Member member, HttpServletResponse response);

    AccessTokenResponseDto reissueAccessToken(String accessToken, String refreshToken);


    AccessTokenResponseDto createResetPasswordJwt(String email);

    String newAccessTokenByProfile(String accessToken, String refreshToken, Member member, Long profileId);
}
