package com.example.PetApp.config;

import com.example.PetApp.redis.util.RedisUtil;
import com.example.PetApp.security.jwt.filter.JwtAuthenticationFilter;
import com.example.PetApp.security.jwt.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {

    private final JwtAuthenticationProvider authenticationProvider;
    private final RedisUtil redisUtil;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        builder.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager,redisUtil),
                UsernamePasswordAuthenticationFilter.class
        ).authenticationProvider(authenticationProvider);
    }
}
