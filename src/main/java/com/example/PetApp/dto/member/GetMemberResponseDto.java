package com.example.PetApp.dto.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMemberResponseDto {

    private String memberName;

    private String memberImageUrl;
}
