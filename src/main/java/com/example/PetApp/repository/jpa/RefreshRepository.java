package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByMemberMemberId(Long memberId);

    void deleteByMember(Member member);

    Optional<RefreshToken> findByMemberMemberId(Long memberId);

}
