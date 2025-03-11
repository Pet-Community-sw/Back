package com.example.PetApp.repository;

import com.example.PetApp.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    public void deleteByMemberId(long memberId);
}
