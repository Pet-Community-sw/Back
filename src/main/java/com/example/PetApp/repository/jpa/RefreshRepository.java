package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    public void deleteByMemberMemberId(Long memberId);

    Optional<RefreshToken> findByMemberMemberId(Long memberId);

}
