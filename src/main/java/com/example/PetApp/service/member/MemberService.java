package com.example.PetApp.service.member;

import com.example.PetApp.dto.member.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface MemberService {
    MemberSignResponseDto createMember(MemberSignDto memberSignDto);

    LoginResponseDto login(LoginDto loginDto, HttpServletResponse response);

    FindByIdResponseDto findById(String phoneNumber);

    void sendEmail(SendEmailDto sendEmailDto);

    void logout(String accessToken);

    AccessTokenResponseDto verifyCode(String email, String code);

    void resetPassword(ResetPasswordDto resetPasswordDto, String email);


    GetMemberResponseDto getMember(Long memberId, String email);

    void deleteMember(String email);

    void createFcmToken(FcmTokenDto fcmTokenDto);
}
