package com.example.PetApp.service.token;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.AccessTokenResponseDto;
import com.example.PetApp.dto.member.LoginResponseDto;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.exception.UnAuthorizedException;
import com.example.PetApp.mapper.MemberMapper;
import com.example.PetApp.repository.jpa.RoleRepository;
import com.example.PetApp.util.RedisUtil;
import com.example.PetApp.repository.jpa.RefreshRepository;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImp implements TokenService {//리펙토링 필요.

    private final RefreshRepository refreshRepository;
    private final JwtTokenizer jwtTokenizer;
    private final RedisUtil redisUtil;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public LoginResponseDto save(Member member) {
        Optional<RefreshToken> byMember = refreshRepository.findByMember(member);
        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());


        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), null, member.getEmail(),roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getEmail(), roles);

        if (byMember.isEmpty()) {
            RefreshToken refreshToken1 = RefreshToken.builder()
                    .member(member)
                    .refreshToken(refreshToken)
                    .build();
            refreshRepository.save(refreshToken1);
        } else {
            byMember.get().setRefreshToken(refreshToken);
        }
        log.info("로그인 요청 성공");
        return MemberMapper.toLoginResponseDto(member, accessToken);
    }


    @Transactional
    @Override
    public AccessTokenResponseDto accessToken(String accessToken) {
        log.info("에세스 토큰 재요청.");
        String token = accessToken.split(" ")[1];
        Claims claims = getClaimsFromToken(token,TokenType.ACCESS);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        RefreshToken refreshToken = refreshRepository.findByMemberMemberId(memberId)
               .orElseThrow(()->new NotFoundException("refreshToken이 없음 다시로그인."));
        return reissueAccessToken(accessToken, claims, memberId, refreshToken);
    }

    @Override
    public AccessTokenResponseDto createResetPasswordJwt(String email) {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1L, "ROLE_USER"));
        String resetPasswordToken = jwtTokenizer.createResetPasswordToken(email, roles.stream().map(Role::getName).collect(Collectors.toList()));
        return new AccessTokenResponseDto(resetPasswordToken);
    }


    @Transactional
    @Override
    public void deleteRefreshToken(String accessToken) {
        log.info("deleteRefreshToken 요청");
        String[] arr = accessToken.split(" ");
        Claims claims = jwtTokenizer.parseAccessToken(arr[1]);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        refreshRepository.deleteByMemberMemberId(memberId);
        blacklistAccessToken(accessToken);
    }

    @NotNull
    private AccessTokenResponseDto reissueAccessToken(String accessToken, Claims claims, Long memberId, RefreshToken refreshToken) {
        if (jwtTokenizer.isTokenExpired("refresh", refreshToken.getRefreshToken())) {
            throw new UnAuthorizedException("로그인 다시 해야됨.");
        } else {
            Claims claims1 = getClaimsFromToken(accessToken, TokenType.REFRESH);
            List<String> roles = (List<String>) claims1.get("roles");
            String email = claims1.getSubject();
            Optional<Object> profileId = Optional.ofNullable(claims.get("profileId"));//refresh에서 profileId를 꺼내는것이 보안상 좋을 듯한데
            String newAccessToken;//getProfileId를 했을 때 null이면 일반 토큰 있으면 profile토큰
            blacklistAccessToken(accessToken);//access시간이랑 같게 해야됨. 받았던 accesstoken을 유효하지 않게함.
            if (profileId.isEmpty()) {
                newAccessToken = jwtTokenizer.createAccessToken(memberId, null, email, roles);
            }else
                newAccessToken = jwtTokenizer.createAccessToken(memberId, Long.valueOf(profileId.toString()), email, roles);//profile이있으면 붙혀서 반환.
            return new AccessTokenResponseDto(newAccessToken);
        }
    }

    private void blacklistAccessToken(String accessToken) {
        redisUtil.createData(accessToken, "blacklist", 30 * 60L);
    }

    private Claims getClaimsFromToken(String token, TokenType tokenType) {
        if (tokenType == TokenType.ACCESS) {
            try {
                return jwtTokenizer.parseAccessToken(token);
            } catch (ExpiredJwtException e) {
                return e.getClaims();
            }
        } else if (tokenType == TokenType.REFRESH) {
            try {
                return jwtTokenizer.parseRefreshToken(token);
            } catch (ExpiredJwtException e) {
                return e.getClaims();
            }
        }else
            throw new IllegalArgumentException("잘못된 tokenType");
    }

}
