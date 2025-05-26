package com.example.PetApp.controller;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.*;
import com.example.PetApp.service.fcm.FcmTokenService;
import com.example.PetApp.service.member.EmailService;
import com.example.PetApp.service.member.MemberService;
import com.example.PetApp.service.member.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final FcmTokenService fcmTokenService;


    @PostMapping("/signup")
    public ResponseEntity signUp(@ModelAttribute @Valid MemberSignDto memberSignDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
        }
        if (memberService.findByEmail(memberSignDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 가입된 회원입니다.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.save(memberSignDto));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {
        log.info("로그인 요청");
        Optional<Member> member = memberService.findByEmail(loginDto.getEmail());
        if (member.isEmpty() && !passwordEncoder.matches(member.get().getPassword(), loginDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        LoginResponseDto loginResponseDto = tokenService.save(member.get());
        return ResponseEntity.ok().body(loginResponseDto);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
        tokenService.deleteRefreshToken(accessToken);
        return ResponseEntity.ok().body("로그아웃 되었습니다.");
        //로그아웃시 redis에 accesstoken값을 저장하고 필터에서 redis에 있으면 로그아웃된 유저임. redis에 시간 설정을 하여 accesstoken값도 없어 지게함.
    }

    @GetMapping("/find-id")
    public ResponseEntity findById(@RequestParam String phoneNumber) {
        return memberService.findByPhoneNumber(phoneNumber);

    }

    @PostMapping("/send-email")
    public ResponseEntity sendEmail(@RequestBody SendEmailDto sendEmailDto) {
        Optional<Member> member = memberService.findByEmail(sendEmailDto.getEmail());
        if (member.isEmpty()) {
            return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
        }
        emailService.sendMail(member.get().getEmail());
        return ResponseEntity.ok().body("해당 이메일로 인증번호 전송했습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity verifyCode(@RequestBody AuthCodeDto authCodeDto) {
        return emailService.verifyCode(authCodeDto.getEmail(), authCodeDto.getCode());
    }


    @PutMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto,
                                        BindingResult bindingResult,
                                        Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }
        return memberService.resetPassword(resetPasswordDto,authentication.getPrincipal().toString());
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<?> getMember(@PathVariable Long memberId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return memberService.getMember(memberId, email);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long memberId, Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return memberService.deleteMember(memberId, email);
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<?> createFcmToken(@RequestBody FcmTokenDto fcmTokenDto) {
        return fcmTokenService.createFcmToken(fcmTokenDto);
    }
}
