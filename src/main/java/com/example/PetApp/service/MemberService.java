package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Role;
import com.example.PetApp.repository.MemberRepository;
import com.example.PetApp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    public Member save(Member member) {
        Role role = roleRepository.findByName("ROLE_USER").get();
        member.addRole(role);
        memberRepository.save(member);
        return member;
    }

    public Optional<Member> getMemberEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
