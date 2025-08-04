package com.example.PetApp.query;

import com.example.PetApp.domain.Member;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.NormalPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberQueryService {
    private final MemberRepository memberRepository;
    private final NormalPostRepository normalPostRepository;

    public Member findByMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }

    public Member findByMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }
}
