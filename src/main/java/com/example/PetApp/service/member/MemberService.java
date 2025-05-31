package com.example.PetApp.service.member;

import com.example.PetApp.dto.member.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    ResponseEntity<?> createMember(MemberSignDto memberSignDto);

    ResponseEntity<?> login(LoginDto loginDto);

    ResponseEntity<?> findById(String phoneNumber);

    ResponseEntity<?> sendEmail(SendEmailDto sendEmailDto);

    ResponseEntity<String> logout(String accessToken);

    ResponseEntity<?> verifyCode(String email, String code);

    ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto, String email);

    ResponseEntity<?> getMember(Long memberId, String email);

    ResponseEntity<String> deleteMember(String email);

    ResponseEntity<?> createFcmToken(FcmTokenDto fcmTokenDto);
}
