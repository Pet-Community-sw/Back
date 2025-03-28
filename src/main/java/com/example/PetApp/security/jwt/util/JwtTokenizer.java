package com.example.PetApp.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenizer {

    private final byte[] accessKey;

    private final byte[] refreshKey;

    private static final Long ACCESS_TOKEN_EXPIRE_COUNT =  5*24*60*60*1000L;
    private static final Long REFRESH_TOKEN_EXPIRE_COUNT = 7*24*60*60*1000L;


    public JwtTokenizer(@Value("${jwt.accessKey}") String accessKey, @Value("${jwt.refreshKey}") String refreshKey) {
        this.accessKey = accessKey.getBytes(StandardCharsets.UTF_8);
        this.refreshKey = refreshKey.getBytes(StandardCharsets.UTF_8);
    }

    private String createToken(Long id, String email, List<String> roles, Long expire, byte[] key) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        claims.put("memberId", id);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expire))
                .signWith(getSigningKey(key))
                .compact();
    }

    public String createAccessToken(Long id, String email, List<String> roles) {
        return createToken(id, email, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessKey);
    }

    public String createRefreshToken(Long id, String email, List<String> roles) {
        return createToken(id, email, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshKey);
    }

    private Claims parseToken(String token, byte[] key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        try{
            return parseToken(token, refreshKey).getExpiration().before(new Date());
        }catch (ExpiredJwtException e) {
            return true; // 토큰이 만료되었으면 true 반환
        } catch (Exception e) {
            return true; // 유효하지 않은 토큰도 만료된 것으로 처리
        }
    }

    public Claims parseAccessToken(String token) {
        return parseToken(token, accessKey);
    }

    public Claims parseRefreshToken(String token) {
        return parseToken(token, refreshKey);
    }
    private Key getSigningKey(byte[] key) {
        return Keys.hmacShaKeyFor(key);
    }
    
}
