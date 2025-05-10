package com.example.PetApp.service.fcm;

import com.example.PetApp.dto.member.FcmTokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface FcmService {
    ResponseEntity<?> createFcmToken(FcmTokenDto fcmTokenDto);
}
