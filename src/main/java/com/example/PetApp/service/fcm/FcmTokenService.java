package com.example.PetApp.service.fcm;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.FcmTokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface FcmTokenService {

    void createFcmToken(Member member, String token);
}
