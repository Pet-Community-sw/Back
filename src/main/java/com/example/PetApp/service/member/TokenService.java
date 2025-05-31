package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.LoginResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    void deleteRefreshToken(String accessToken);

    LoginResponseDto save(Member member);
}
