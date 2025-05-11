package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.MemberStatusDto;
import com.example.PetApp.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberStatusService {

    private final StringRedisTemplate stringRedisTemplate;
    private final MemberRepository memberRepository;

    public ResponseEntity<?> updateMemberStatus(MemberStatusDto statusDto) {
        Optional<Member> member = memberRepository.findById(statusDto.getMemberId());
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저 없음.");
        }

        if (statusDto.getStatusType() == MemberStatusDto.StatusType.ONLINE) {
            stringRedisTemplate.opsForSet().add("foreGroundMembers:", statusDto.getMemberId().toString());
            return ResponseEntity.ok().body("foreGroundMember");
        } else if (statusDto.getStatusType() == MemberStatusDto.StatusType.OFFLINE) {
            stringRedisTemplate.opsForSet().remove("foreGroundMembers", statusDto.getMemberId().toString());
            return ResponseEntity.ok().body("backGroundMember");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청.");
    }
}
