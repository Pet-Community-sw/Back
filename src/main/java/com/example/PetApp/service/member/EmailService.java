package com.example.PetApp.service.member;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    ResponseEntity<?> sendMail(String email);

    ResponseEntity<?> verifyCode(String email, String code);
}
