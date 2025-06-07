package com.example.PetApp.service;


import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.*;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.service.email.EmailService;
import com.example.PetApp.service.member.MemberServiceImp;
import com.example.PetApp.service.token.TokenService;
import com.example.PetApp.util.imagefile.FileImageKind;
import com.example.PetApp.util.imagefile.FileUploadUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImpTest {

    @InjectMocks
    private MemberServiceImp memberServiceImp;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private EmailService emailService;


    @Test
    @DisplayName("signup_성공")
    void test1() {
        //given
        MemberSignDto memberSignDto = MemberSignDto.builder()
                .name("최선재")
                .email("chltjswo789@naver.com")
                .password("fpdlswj365!")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
        //when
        MemberSignResponseDto member = memberServiceImp.createMember(memberSignDto);

        //then
        assertThat(member).isNotNull();
    }

    @Test
    @DisplayName("signup_비밀번호 형식_실패")
    void test2() {
        //given

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        MemberSignDto memberSignDto = MemberSignDto.builder()
                .name("최선재")
                .email("chltjswo890@naver.com")
                .password("fpdlswj365")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
        //when
        Set<ConstraintViolation<MemberSignDto>> validate = validator.validate(memberSignDto);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
    }

    @Test
    @DisplayName("사진 저장_이미지가 null이면 기본경로를 반환")
    void test3() {
        // given

        // when
        String result = FileUploadUtil.fileUpload(null, "/uploads", FileImageKind.MEMBER);

        // then
        assertThat(result).isEqualTo("/basic/Profile_avatar_placeholder_large.png");
    }

    @Test
    @DisplayName("login_성공")
    void test4() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("chltjswo789@naver.com")
                .password("1234")
                .build();

        Member member = createFakeMember();

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .name("최선재")
                .accessToken("accessToken")
                .build();

        when(memberRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("1234", "fpdlswj365!")).thenReturn(true);
        when(tokenService.save(member)).thenReturn(loginResponseDto);

        //when
        LoginResponseDto result = memberServiceImp.login(loginDto);
        //then
        assertThat(result.getName()).isEqualTo(member.getName());
    }


    @Test
    @DisplayName("login_실패")
    void test5() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("chltjswo789@naver.com")
                .password("fpdlswj365!")
                .build();

        when(memberRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());
        //when&then
        assertThatThrownBy(() -> memberServiceImp.login(loginDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("이메일 혹은 비밀번호가 일치하지 않습니다.");

    }

    @Test
    @DisplayName("sendEmail_성공")
    void test6() {
        //given
        SendEmailDto sendEmailDto = new SendEmailDto("chltjswo789@naver.com");
        Member member = createFakeMember();

        when(memberRepository.findByEmail(sendEmailDto.getEmail())).thenReturn(Optional.of(member));

        //when
        memberServiceImp.sendEmail(sendEmailDto);

        //then
        verify(emailService).sendMail(sendEmailDto.getEmail());
    }

    @Test
    @DisplayName("resetPassword_성공")
    void test7() {
        //given
        String email = "chltjswo789@naver.com";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto("fpdlswj365!!");
        Member member = createFakeMember();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(resetPasswordDto.getNewPassword(), member.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(resetPasswordDto.getNewPassword())).thenReturn(resetPasswordDto.getNewPassword());

        //when
        memberServiceImp.resetPassword(resetPasswordDto, email);

        //then
        assertThat(member.getPassword()).isEqualTo(resetPasswordDto.getNewPassword());
    }

    @Test
    @DisplayName("resetPassword_실패")
    void test8() {
        //given
        String email = "chltjswo789@naver.com";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto("fpdlswj365!");
        Member member = createFakeMember();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(resetPasswordDto.getNewPassword(), member.getPassword())).thenReturn(true);

        //when & then
        assertThatThrownBy(() -> memberServiceImp.resetPassword(resetPasswordDto, email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("전 비밀번호와 다르게 설정해야합니다.");
    }

    @Test
    @DisplayName("sendEmail_실패")

    private static Member createFakeMember() {
        return Member.builder()
                .memberId(1L)
                .name("최선재")
                .email("chltjswo789@naver.com")
                .password("fpdlswj365!")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
    }


}
