package com.example.PetApp.service.member;

import com.example.PetApp.dto.member.*;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    MemberSignResponseDto createMember(MemberSignDto memberSignDto);

    LoginResponseDto login(LoginDto loginDto);

    FindByIdResponseDto findById(String phoneNumber);

    void sendEmail(SendEmailDto sendEmailDto);

    void logout(String accessToken);

    void verifyCode(String email, String code);

//    void resetPassword(ResetPasswordDto resetPasswordDto, String email);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    GetMemberResponseDto getMember(Long memberId, String email);

    void deleteMember(String email);

    void createFcmToken(FcmTokenDto fcmTokenDto);
}
