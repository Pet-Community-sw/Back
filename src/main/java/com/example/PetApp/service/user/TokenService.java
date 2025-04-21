package com.example.PetApp.service.user;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.LoginResponseDto;
import com.example.PetApp.util.RedisUtil;
import com.example.PetApp.repository.jpa.RefreshRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RefreshRepository refreshRepository;
    private final JwtTokenizer jwtTokenizer;
    private final RedisUtil redisUtil;
    @Transactional
    public LoginResponseDto save(Member member) {
        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), null, member.getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getEmail(), roles);

        RefreshToken refreshToken1 = RefreshToken.builder()
                .member(member)
                .refreshToken(refreshToken)
                .build();
        refreshRepository.save(refreshToken1);
        log.info("로그인 요청 성공");
        return LoginResponseDto.builder()
                .name(member.getName())
                .accessToken(accessToken)
                .build();
    }
    @Transactional
    public ResponseEntity<?> accessToken(String accessToken) {
        String[] arr = accessToken.split(" ");
        Claims claims;
        try {
            claims = jwtTokenizer.parseAccessToken(arr[1]);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims(); // 만료된 경우에도 Claims 추출 가능
        }
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        Optional<RefreshToken> refreshToken = refreshRepository.findByMemberMemberId(memberId);

        if (refreshToken.isEmpty()||jwtTokenizer.isTokenExpired("refresh",refreshToken.get().getRefreshToken())) {
            return ResponseEntity.badRequest().body("다시 로그인 해주세요");
        } else {
            Claims claims1 = jwtTokenizer.parseRefreshToken(refreshToken.get().getRefreshToken());
            String email = claims1.getSubject();
            Long profileId = Long.valueOf(claims.get("profileId").toString());//refresh에서 profileId를 꺼내는것이 보안상 좋을 듯한데
            //accesstoken주기 시간을 줄이면 될것같은데 성능 문제?
            List<String> roles = (List<String>) claims1.get("roles");
            Map<String, String> message = new HashMap<>();//getProfileId를 했을 때 null이면 일반 토큰 있으면 profile토큰
            redisUtil.createData(accessToken, "blacklist", 30 * 60L);//access시간이랑 같게 해야됨. 받았던 accesstoken을 유효하지 않게함.
            if (profileId == null) {
                message.put("accessToken", jwtTokenizer.createAccessToken(memberId, null, email, roles));
            }else
                message.put("accessToken", jwtTokenizer.createAccessToken(memberId, profileId, email, roles));//profile이있으면 붙혀서 반환.
            return ResponseEntity.ok().body(message);
        }
    }

    @Transactional
    public void deleteRefreshToken(String accessToken) {
        String[] arr = accessToken.split(" ");
        Claims claims = jwtTokenizer.parseAccessToken(arr[1]);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        refreshRepository.deleteByMemberMemberId(memberId);
        redisUtil.createData(accessToken, "blacklist", 30 * 60L);//access시간이랑 같게 해야됨.
    }

    @Transactional
    public Optional<RefreshToken> findByMemberId(long memberId) {
        return refreshRepository.findByMemberMemberId(memberId);
    }
}
