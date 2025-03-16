package com.example.PetApp.controller;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.*;
import com.example.PetApp.redis.util.RedisUtil;
import com.example.PetApp.security.jwt.util.JwtTokenizer;
import com.example.PetApp.service.EmailService;
import com.example.PetApp.service.MemberService;
import com.example.PetApp.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final RedisUtil redisUtil;


    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody @Valid MemberSignDto memberSignDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
        if (memberService.findByEmail(memberSignDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body("이미 가입된 회원입니다.");
        }
        Member member = new Member();
        member.setName(memberSignDto.getName());
        member.setEmail(memberSignDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberSignDto.getPassword()));
        member.setPhoneNumber(memberSignDto.getPhoneNumber());
        memberService.save(member);

        MemberSignResponseDto memberSignResponseDto = MemberSignResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(memberSignResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {
        Optional<Member> member = memberService.findByEmail(loginDto.getEmail());
        if (member == null && !passwordEncoder.matches(member.get().getPassword(), loginDto.getPassword())) {
            return ResponseEntity.badRequest().body("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }

        List<String> roles = member.get().getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.get().getMemberId(), member.get().getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.get().getMemberId(), member.get().getEmail(), roles);

        RefreshToken refreshToken1 = new RefreshToken();
        refreshToken1.setMemberId(member.get().getMemberId());
        refreshToken1.setValue(refreshToken);
        refreshTokenService.save(refreshToken1);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .name(member.get().getName())
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok().body(loginResponseDto);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
        String[] arr = accessToken.split(" ");
        Claims claims = jwtTokenizer.parseAccessToken(arr[1]);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        refreshTokenService.deleteByMemberId(memberId);
        redisUtil.createData(accessToken, "blacklist", 30 * 60L);
        return ResponseEntity.ok().body("로그아웃 되었습니다.");

        //로그아웃시 redis에 accesstoken값을 저장하고 필터에서 redis에 있으면 로그아웃된 유저임. redis에 시간 설정을 하여 accesstoken값도 없어 지게함.
    }

    @PostMapping("/find-id")
    public ResponseEntity findById(@RequestBody FindByIdDto findByIdDto) {
        Optional<Member> member = memberService.findByPhoneNumber(findByIdDto.getPhoneNumber());
        if (member.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 유저는 없는 유저입니다. 회원가입 해주세요.");
        }
        FindByIdResponseDto findByIdResponseDto = new FindByIdResponseDto();
        findByIdResponseDto.setEmail(member.get().getEmail());
        return ResponseEntity.ok().body(findByIdResponseDto.getEmail());
    }

    @PostMapping("/send-email")
    public ResponseEntity findByPassword(@RequestBody SendEmailDto sendEmailDto) {
        Optional<Member> member = memberService.findByEmail(sendEmailDto.getEmail());
        if (member.isEmpty()) {
            return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
        }
        emailService.sendMail(member.get().getEmail());
        return ResponseEntity.ok().body("해당 이메일로 인증번호 전송했습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity verifyCode(@RequestBody AuthCodeDto authCodeDto) {
        return emailService.verifyCode(authCodeDto.getEmail(), authCodeDto.getCode());
    }

    @PostMapping("/accessToken")
    public ResponseEntity accessToken(@RequestHeader("Authorization") String accessToken) {
        String[] arr = accessToken.split(" ");
        Claims claims;
        try {
            claims = jwtTokenizer.parseAccessToken(arr[1]);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims(); // 만료된 경우에도 Claims 추출 가능
        }
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        RefreshToken refreshToken = refreshTokenService.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("로그인 해주세요."));
        if (jwtTokenizer.isTokenExpired(refreshToken.getValue())) {
            return ResponseEntity.badRequest().body("다시 로그인 해주세요");
        }else {
            Claims claims1 = jwtTokenizer.parseRefreshToken(refreshToken.getValue());
            String email = claims1.getSubject();
            List<String> roles = (List<String>) claims1.get("roles");
            Map<String, String> message = new HashMap<>();
            message.put("accessToken", jwtTokenizer.createAccessToken(memberId, email, roles));
            return ResponseEntity.ok().body(message);
        }

    }
}
