package com.example.PetApp.security.jwt.filter;

import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.redis.util.RedisUtil;
import com.example.PetApp.security.jwt.exception.JwtExceptionCode;
import com.example.PetApp.security.jwt.token.JwtAuthenticationToken;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.example.PetApp.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final RedisUtil redisUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return StringUtils.startsWithIgnoreCase(request.getRequestURI(), "/members/accessToken");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,

                                    FilterChain filterChain) throws ServletException, IOException {
        String token = "";
        try {
            token = getToken(request);
            if (StringUtils.hasText(token)) {
                authenticationToken(token);
            }
            filterChain.doFilter(request, response);
        } catch (NullPointerException | IllegalStateException e) {
            request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
            log.error("Not found Token // token : {}", token);
            log.error("Set Request Exception Code : {}", request.getAttribute("exception"));
            throw new BadCredentialsException("throw new not found token exception");
        } catch (SecurityException | MalformedJwtException e) {
            request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
            log.error("Invalid Token // token : {}", token);
            log.error("Set Request Exception Code : {}", request.getAttribute("exception"));
            throw new BadCredentialsException("throw new invalid token exception");
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
            log.error("EXPIRED Token // token : {}", token);
            log.error("Set Request Exception Code : {}", request.getAttribute("exception"));
            throw new BadCredentialsException("throw new expired token exception");
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
            log.error("Unsupported Token // token : {}", token);
            log.error("Set Request Exception Code : {}", request.getAttribute("exception"));
            throw new BadCredentialsException("throw new unsupported token exception");
        } catch (Exception e) {
            log.error("====================================================");
            log.error("JwtFilter - doFilterInternal() 오류 발생");
            log.error("token : {}", token);
            log.error("Exception Message : {}", e.getMessage());
            log.error("Exception StackTrace : {");
            e.printStackTrace();
            log.error("}");
            log.error("====================================================");
            throw new BadCredentialsException("throw new exception");
        }
    }

    private void authenticationToken(String token) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext()
                .setAuthentication(authenticate);

    }

    public String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (redisUtil.existData(authorization)) {
            throw new BadCredentialsException("로그아웃된 토큰입니다.");
        }
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer")){
            String[] arr = authorization.split(" ");
            return arr[1];
        }
        return null;
    }
}