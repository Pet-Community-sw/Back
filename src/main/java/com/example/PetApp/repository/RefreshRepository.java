package com.example.PetApp.repository;

import com.example.PetApp.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    public void deleteByMemberId(long memberId);

    Optional<RefreshToken> findByMemberId(long memberId);

}
