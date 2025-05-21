package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStatusService {

    private final StringRedisTemplate stringRedisTemplate;
    private final MemberRepository memberRepository;

    public ResponseEntity<?> updateMemberStatus(String email) {
        Member member = memberRepository.findByEmail(email).get();
        stringRedisTemplate.opsForSet().add("foreGroundMembers:", member.getMemberId().toString());
        return ResponseEntity.ok().body("foreGroundMember");
        }
    }
