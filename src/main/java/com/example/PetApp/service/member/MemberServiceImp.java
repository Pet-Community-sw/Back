package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.*;
import com.example.PetApp.mapper.MemberMapper;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.RoleRepository;
import com.example.PetApp.service.fcm.FcmTokenService;
import com.example.PetApp.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImp implements MemberService{

    @Value("${spring.dog.member.image.upload}")
    private String memberUploadDir;

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final FcmTokenService fcmTokenService;

    @Transactional
    @Override
    public ResponseEntity<?> createMember(MemberSignDto memberSignDto) {
        log.info("createMember 요청 : {}",memberSignDto.toString());
        Role role = roleRepository.findByName("ROLE_USER").get();
        String imageFileName = FileUploadUtil.fileUpload(memberSignDto.getMemberImageUrl(), memberUploadDir);
        Member member = MemberMapper.toEntity(memberSignDto, passwordEncoder.encode(memberSignDto.getPassword()), imageFileName);
        member.addRole(role);
        memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberSignResponseDto.builder() 이거 바꿔야돰.
                .memberId(member.getMemberId())
                .name(member.getName())
                .build());
    }

    @Transactional
    @Override
    public ResponseEntity<?> login(LoginDto loginDto) {
        log.info("login 요청 : {}", loginDto.toString());
        Optional<Member> member = memberRepository.findByEmail(loginDto.getEmail());
        if (member.isEmpty() || !passwordEncoder.matches(member.get().getPassword(), loginDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        return tokenService.save(member.get());
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<?> findById(String phoneNumber) {
        log.info("findById 요청 phonNumber : {}", phoneNumber);
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저는 없는 유저입니다. 회원가입 해주세요.");
        }else{
            return ResponseEntity.ok(Map.of("email", member.get().getEmail()));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<?> sendEmail(SendEmailDto sendEmailDto) {
        log.info("sendEmail 요청 : {}",sendEmailDto.getEmail());
        Optional<Member> member = memberRepository.findByEmail(sendEmailDto.getEmail());
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일입니다.");
        }
        return emailService.sendMail(member.get().getEmail());
    }


    @Override
    public ResponseEntity<String> logout(String accessToken) {
        log.info("logout 요청");
        return tokenService.deleteRefreshToken(accessToken);
    }

    @Override
    public ResponseEntity<?> verifyCode(String email, String code) {//sendEmail할 때 이메일 유효성 검사 했으므로 안해줘도 됨.
        return emailService.verifyCode(email, code);
    }

    @Transactional
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        if (passwordEncoder.matches(resetPasswordDto.getNewPassword(),member.getPassword())) {
            return ResponseEntity.badRequest().body("전 비밀번호와 다르게 설정해야합니다.");
        } else {
            member.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습나다.");
        }
    }

    @Transactional(readOnly = true)//상세 멤버 프로필 추가랑 어떤거 해야할지 해야됨. 여기에 자기가 쓴 게시물, 산책길 추천, 후기 추가해야할듯.
    @Override
    public ResponseEntity<?> getMember(Long memberId, String email) {
        log.info("getMember 요청 : {}", memberId);
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저는 없습니다.");
        }
        GetMemberResponseDto getMemberResponseDto = GetMemberResponseDto.builder()
                .memberName(member.get().getName())
                .memberImageUrl(member.get().getMemberImageUrl())
                .build();
        return ResponseEntity.ok(getMemberResponseDto);
    }

    @Transactional
    @Override
    public ResponseEntity<String> deleteMember(String email) {
        log.info("deleteMember 요청 email:{}", email);
        Member member = memberRepository.findByEmail(email).get();
        memberRepository.delete(member);
        return ResponseEntity.ok().body("삭제 완료.");
    }

    @Transactional
    @Override
    public ResponseEntity<?> createFcmToken(FcmTokenDto fcmTokenDto) {
        log.info("createFcmToken 요청 : {}",fcmTokenDto.getMemberId());
        Optional<Member> member = memberRepository.findById(fcmTokenDto.getMemberId());
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저는 없습니다.");
        }
        return fcmTokenService.createFcmToken(member.get(), fcmTokenDto.getFcmToken());
    }
}
