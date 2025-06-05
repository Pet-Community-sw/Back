package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.*;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.exception.UnAuthorizedException;
import com.example.PetApp.mapper.MemberMapper;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.service.email.EmailService;
import com.example.PetApp.service.fcm.FcmTokenService;
import com.example.PetApp.service.token.TokenService;
import com.example.PetApp.util.imagefile.FileUploadUtil;
import com.example.PetApp.util.imagefile.FileImageKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImp implements MemberService{

    @Value("${spring.dog.member.image.upload}")
    private String memberUploadDir;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final FcmTokenService fcmTokenService;

    @Transactional
    @Override
    public MemberSignResponseDto createMember(MemberSignDto memberSignDto) {
        log.info("createMember 요청 : {}",memberSignDto.toString());
        if (memberRepository.existsByEmail(memberSignDto.getEmail())) {
            throw new ConflictException("이미 가입된 회원입니다.");
        }
        String imageFileName = FileUploadUtil.fileUpload(memberSignDto.getMemberImageUrl(), memberUploadDir, FileImageKind.MEMBER);
        Member member = MemberMapper.toEntity(memberSignDto, passwordEncoder.encode(memberSignDto.getPassword()), imageFileName);
        memberRepository.save(member);
        return new MemberSignResponseDto(member.getMemberId());
    }

    @Transactional
    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        log.info("login 요청 : {}", loginDto.toString());
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new NotFoundException("이메일 혹은 비밀번호가 일치하지 않습니다."));
        if (!passwordEncoder.matches(loginDto.getPassword(),member.getPassword())) {
            throw new UnAuthorizedException("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        return tokenService.save(member);
    }

    @Override
    public AccessTokenResponseDto verifyCode(String email, String code) {//sendEmail할 때 이메일 유효성 검사 했으므로 안해줘도 됨.
        emailService.verifyCode(email, code);
        return tokenService.createResetPasswordJwt(email);
    }

    @Transactional(readOnly = true)
    @Override
    public FindByIdResponseDto findById(String phoneNumber) {
        log.info("findById 요청 phonNumber : {}", phoneNumber);
        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("해당 유저는 없는 유저입니다. 회원가입 해주세요."));
        return new FindByIdResponseDto(member.getEmail());
    }

    @Transactional(readOnly = true)
    @Override
    public void sendEmail(SendEmailDto sendEmailDto) {
        log.info("sendEmail 요청 : {}",sendEmailDto.getEmail());
        Member member = memberRepository.findByEmail(sendEmailDto.getEmail())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다."));
        emailService.sendMail(member.getEmail());
    }


    @Override
    public void logout(String accessToken) {
        log.info("logout 요청");
        tokenService.deleteRefreshToken(accessToken);
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(()->new NotFoundException("없는 유저입니다."));
    }

//    @Transactional
//    @Override
//    public void resetPassword(ResetPasswordDto resetPasswordDto, String email) {
//        Member member = memberRepository.findByEmail(email).get();
//        if (passwordEncoder.matches(resetPasswordDto.getNewPassword(),member.getPassword())) {
//            throw new IllegalArgumentException("전 비밀번호와 다르게 설정해야합니다.");
//        } else {
//            member.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
//        }
//    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, String email) {
        log.info("resetPassword 요청 email : {}, newPassword : {}", email, resetPasswordDto.getNewPassword());
        Member member = memberRepository.findByEmail(email).get();
        if (passwordEncoder.matches(resetPasswordDto.getNewPassword(),member.getPassword())) {
            throw new IllegalArgumentException("전 비밀번호와 다르게 설정해야합니다.");
        } else {
            member.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        }
    }

    @Transactional(readOnly = true)//상세 멤버 프로필 추가랑 어떤거 해야할지 해야됨. 여기에 자기가 쓴 게시물, 산책길 추천, 후기 추가해야할듯.
    @Override
    public GetMemberResponseDto getMember(Long memberId, String email) {
        log.info("getMember 요청 : {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));

        return GetMemberResponseDto.builder()
                .memberName(member.getName())
                .memberImageUrl(member.getMemberImageUrl())
                .build();
    }

    @Transactional
    @Override
    public void deleteMember(String email) {
        log.info("deleteMember 요청 email:{}", email);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("없는 회원입니다."));
        memberRepository.delete(member);
    }

    @Transactional
    @Override
    public void createFcmToken(FcmTokenDto fcmTokenDto) {
        log.info("createFcmToken 요청 : {}",fcmTokenDto.getMemberId());
        Member member = memberRepository.findById(fcmTokenDto.getMemberId())
                .orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
        fcmTokenService.createFcmToken(member, fcmTokenDto.getFcmToken());
    }
}
