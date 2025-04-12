package com.example.PetApp.service.user;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.MemberSignDto;
import com.example.PetApp.dto.member.MemberSignResponseDto;
import com.example.PetApp.dto.member.ResetPasswordDto;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.RoleRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.example.PetApp.util.RedisUtil;
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
    private final TokenService refreshTokenService;
    private final RedisUtil redisUtil;


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
    public ResponseEntity resetPassword(ResetPasswordDto resetPasswordDto) {
        Member member = memberRepository.findByEmail(resetPasswordDto.getEmail()).get();
        if (passwordEncoder.matches(member.getPassword(), resetPasswordDto.getNewPassword())) {
            return ResponseEntity.badRequest().body("전 비밀번호와 다르게 설정해야합니다.");
        } else {
            member.setPassword(resetPasswordDto.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습나다.");
        }
    }
}
