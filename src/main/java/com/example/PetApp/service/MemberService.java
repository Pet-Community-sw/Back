package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.MemberSignDto;
import com.example.PetApp.dto.member.MemberSignResponseDto;
import com.example.PetApp.dto.member.ResetPasswordDto;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.RoleRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public MemberSignResponseDto save(MemberSignDto memberSignDto) {
        Role role = roleRepository.findByName("ROLE_USER").get();
        Member member = Member.builder()
                .name(memberSignDto.getName())
                .email(memberSignDto.getEmail())
                .password(passwordEncoder.encode(memberSignDto.getPassword()))
                .phoneNumber(memberSignDto.getPhoneNumber())
                .build();
        if (member.getRoles() == null) {
            member.setRoles(new HashSet<>());
        }
        member.addRole(role);
        memberRepository.save(member);
        return MemberSignResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
    }

    @Transactional
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public ResponseEntity accessToken(String accessToken) {
        String[] arr = accessToken.split(" ");
        Claims claims;
        try {
            claims = jwtTokenizer.parseAccessToken(arr[1]);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims(); // 만료된 경우에도 Claims 추출 가능
        }
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        RefreshToken refreshToken = refreshTokenService.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("로그인 해주세요."));
        if (jwtTokenizer.isTokenExpired(refreshToken.getValue())) {
            return ResponseEntity.badRequest().body("다시 로그인 해주세요");
        } else {
            Claims claims1 = jwtTokenizer.parseRefreshToken(refreshToken.getValue());
            String email = claims1.getSubject();
            List<String> roles = (List<String>) claims1.get("roles");
            Map<String, String> message = new HashMap<>();
            message.put("accessToken", jwtTokenizer.createAccessToken(memberId, email, roles));
            return ResponseEntity.ok().body(message);
        }
    }

    @Transactional
    public ResponseEntity resetPassword(ResetPasswordDto resetPasswordDto) {
        Member member = memberRepository.findByEmail(resetPasswordDto.getEmail()).get();
        if (passwordEncoder.matches(member.getPassword(),resetPasswordDto.getNewPassword())) {
            return ResponseEntity.badRequest().body("전 비밀번호와 다르게 설정해야합니다.");
        }else {
            member.setPassword(resetPasswordDto.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습나다.");
        }
    }
}
