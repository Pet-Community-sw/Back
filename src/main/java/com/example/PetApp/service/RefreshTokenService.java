package com.example.PetApp.service;

import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshRepository refreshRepository;

    @Transactional
    public void save(RefreshToken refreshToken) {
        refreshRepository.save(refreshToken);
    }

    @Transactional
    public void deleteByMemberId(long memberId) {
        refreshRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public Optional<RefreshToken> findByMemberId(long memberId) {
        return refreshRepository.findByMemberId(memberId);
    }
}
