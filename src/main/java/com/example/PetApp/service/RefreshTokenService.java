package com.example.PetApp.service;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.LoginResponseDto;
import com.example.PetApp.redis.util.RedisUtil;
import com.example.PetApp.repository.RefreshRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshRepository refreshRepository;
    private final JwtTokenizer jwtTokenizer;
    private final RedisUtil redisUtil;
    @Transactional
    public LoginResponseDto save(Member member) {
        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), member.getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getEmail(), roles);

        RefreshToken refreshToken1 = RefreshToken.builder()
                .memberId(member.getMemberId())
                .value(refreshToken)
                .build();
        refreshRepository.save(refreshToken1);

        return LoginResponseDto.builder()
                .name(member.getName())
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public void deleteRefreshToken(String accessToken) {
        String[] arr = accessToken.split(" ");
        Claims claims = jwtTokenizer.parseAccessToken(arr[1]);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        refreshRepository.deleteByMemberId(memberId);
        redisUtil.createData(accessToken, "blacklist", 30 * 60L);
    }

    @Transactional
    public Optional<RefreshToken> findByMemberId(long memberId) {
        return refreshRepository.findByMemberId(memberId);
    }
}
