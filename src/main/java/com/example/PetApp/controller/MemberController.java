package com.example.PetApp.controller;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.LoginDto;
import com.example.PetApp.dto.LoginResponseDto;
import com.example.PetApp.dto.MemberSignDto;
import com.example.PetApp.dto.MemberSignResponseDto;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.example.PetApp.service.MemberService;
import com.example.PetApp.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody @Valid MemberSignDto memberSignDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
        if (memberService.findByEmail(memberSignDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body("이미 가입된 회원입니다.");
        }
        Member member = new Member();
        member.setName(memberSignDto.getName());
        member.setEmail(memberSignDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberSignDto.getPassword()));
        member.setPhoneNumber(memberSignDto.getPhoneNumber());
        memberService.save(member);

        MemberSignResponseDto memberSignResponseDto = MemberSignResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(memberSignResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {
        Member member = memberService.findByEmail(loginDto.getEmail());
        if (member == null && !passwordEncoder.matches(member.getPassword(), loginDto.getPassword())) {
            return ResponseEntity.badRequest().body("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }

        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), member.getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getEmail(), roles);

        RefreshToken refreshToken1 = new RefreshToken();
        refreshToken1.setMemberId(member.getMemberId());
        refreshToken1.setValue(refreshToken);
        refreshTokenService.save(refreshToken1);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .name(member.getName())
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok().body(loginResponseDto);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
        String[] arr = accessToken.split(" ");
        Claims claims = jwtTokenizer.parseAccessToken(arr[1]);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        refreshTokenService.deleteByMemberId(memberId);
        return ResponseEntity.ok().body("로그아웃 되었습니다.");
    }
}
