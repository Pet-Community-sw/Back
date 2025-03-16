package com.example.PetApp.dto.member;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSignResponseDto {

    private Long memberId;

    private String name;
}
