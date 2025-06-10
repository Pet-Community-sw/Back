package com.example.PetApp.config;

import com.example.PetApp.security.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .csrf().disable()
                .cors()
                .and()
                .httpBasic().disable()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/image/profiles/**","/image/members/**","/image/posts/**", "/image/basic/**","/favicon.ico").permitAll()
                .mvcMatchers("/ws-stomp/**", "/pub/**", "/sub/**").permitAll()
                .mvcMatchers("/members/signup", "/members/accessToken", "/members/login",
        "/members/find-id", "/members/send-email", "/members/verify-code",
        "/members/reset-password").permitAll()

                .mvcMatchers(GET, "/**").hasAnyRole("USER", "ADMIN")
                .mvcMatchers(POST, "/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .apply(authenticationManagerConfig)
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
