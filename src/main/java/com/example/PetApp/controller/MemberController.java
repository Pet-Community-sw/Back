package com.example.PetApp.controller;

import com.example.PetApp.dto.member.*;
import com.example.PetApp.service.fcm.FcmTokenService;
import com.example.PetApp.service.member.EmailService;
import com.example.PetApp.service.member.EmailServiceImp;
import com.example.PetApp.service.member.MemberService;
import com.example.PetApp.util.AuthUtil;
import com.example.PetApp.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@ModelAttribute @Valid MemberSignDto memberSignDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getBindingError(bindingResult);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(memberSignDto));
    }


    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto loginDto) {
        return memberService.login(loginDto);
    }

    @GetMapping("/{memberId}")
    public GetMemberResponseDto getMember(@PathVariable Long memberId, Authentication authentication) {
        return memberService.getMember(memberId, AuthUtil.getEmail(authentication));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
        memberService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find-id")
    public FindByIdResponseDto findById(@RequestParam String phoneNumber) {
        return memberService.findById(phoneNumber);
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody SendEmailDto sendEmailDto) {
        memberService.sendEmail(sendEmailDto);
        return ResponseEntity.ok().body("인증번호가 이메일로 전송되었습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody AuthCodeDto authCodeDto) {
        memberService.verifyCode(authCodeDto.getEmail(), authCodeDto.getCode());
        return ResponseEntity.ok().body("인증 성공했습니다.");
    }


    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto,
                                        BindingResult bindingResult,
                                        Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return getBindingError(bindingResult);
        }
        memberService.resetPassword(resetPasswordDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습나다.");
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteMember(Authentication authentication) {
        memberService.deleteMember(AuthUtil.getEmail(authentication));
        return ResponseEntity.ok().body("탈퇴했습니다.");
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<String> createFcmToken(@RequestBody FcmTokenDto fcmTokenDto) {
        memberService.createFcmToken(fcmTokenDto);
        return ResponseEntity.ok().body("fcm토큰 생성완료.");
    }

    @NotNull
    private static ResponseEntity<Map<String, List<String>>> getBindingError(BindingResult bindingResult) {
        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
    }
}
