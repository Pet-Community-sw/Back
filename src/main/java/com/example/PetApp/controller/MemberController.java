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
        return memberService.createMember(memberSignDto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return memberService.login(loginDto);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<?> getMember(@PathVariable Long memberId, Authentication authentication) {
        return memberService.getMember(memberId, AuthUtil.getEmail(authentication));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        return memberService.logout(accessToken);
    }

    @GetMapping("/find-id")
    public ResponseEntity<?> findById(@RequestParam String phoneNumber) {
        return memberService.findById(phoneNumber);
    }

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(@RequestBody SendEmailDto sendEmailDto) {
        return memberService.sendEmail(sendEmailDto);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody AuthCodeDto authCodeDto) {
        return memberService.verifyCode(authCodeDto.getEmail(), authCodeDto.getCode());
    }


    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto,
                                        BindingResult bindingResult,
                                        Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return getBindingError(bindingResult);
        }
        return memberService.resetPassword(resetPasswordDto, AuthUtil.getEmail(authentication));
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteMember(Authentication authentication) {
        return memberService.deleteMember(AuthUtil.getEmail(authentication));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<?> createFcmToken(@RequestBody FcmTokenDto fcmTokenDto) {
        return memberService.createFcmToken(fcmTokenDto);
    }

    @NotNull
    private static ResponseEntity<Map<String, List<String>>> getBindingError(BindingResult bindingResult) {
        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
    }
}
