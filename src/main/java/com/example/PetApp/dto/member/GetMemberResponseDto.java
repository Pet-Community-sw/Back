package com.example.PetApp.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class GetMemberResponseDto {

    private String memberName;

    private String memberImageUrl;
}
