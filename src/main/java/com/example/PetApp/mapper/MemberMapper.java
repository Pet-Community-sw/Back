package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.member.GetMemberResponseDto;
import com.example.PetApp.dto.member.LoginResponseDto;
import com.example.PetApp.dto.member.MemberSignDto;

public class MemberMapper {

    public static Member toEntity(MemberSignDto memberSignDto, String encodedPassword, String imageFileName) {
        return Member.builder()
                .name(memberSignDto.getName())
                .email(memberSignDto.getEmail())
                .password(encodedPassword)
                .phoneNumber(memberSignDto.getPhoneNumber())
                .memberImageUrl(imageFileName)
                .build();
    }

    public static GetMemberResponseDto toGetMemberResponseDto(Member member) {
        return GetMemberResponseDto.builder()
                .memberName(member.getName())
                .memberImageUrl(member.getMemberImageUrl())
                .build();
    }

    public static LoginResponseDto toLoginResponseDto(Member member, String accessToken) {
        return LoginResponseDto.builder()
                .name(member.getName())
                .accessToken(accessToken)
                .build();
    }

}
