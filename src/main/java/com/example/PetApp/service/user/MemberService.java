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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


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
        log.info("회원가입 요청.");
        return MemberSignResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
    }

    @Transactional
    public Optional<Member> findByEmail(String email) {
        log.info("이메일 찾기 요청");
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> findByPhoneNumber(String phoneNumber) {
        log.info("아이디 찾기 요청.");
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저는 없는 유저입니다. 회원가입 해주세요.");
        }else{
            return ResponseEntity.ok(Map.of("email", member.get().getEmail()));

        }

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
