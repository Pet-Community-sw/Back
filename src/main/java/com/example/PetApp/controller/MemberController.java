package com.example.PetApp.controller;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.MemberSignDto;
import com.example.PetApp.dto.MemberSignResponseDto;
import com.example.PetApp.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody @Valid MemberSignDto memberSignDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
        if (memberService.getMemberEmail(memberSignDto.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("이미 가입된 회원입니다.");
        }
        Member member = new Member();
        member.setName(memberSignDto.getName());
        member.setEmail(memberSignDto.getEmail());
        member.setPassword(memberSignDto.getPassword());
        member.setPhoneNumber(memberSignDto.getPhoneNumber());
        memberService.save(member);

        MemberSignResponseDto memberSignResponseDto = MemberSignResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(memberSignResponseDto);
    }
}
