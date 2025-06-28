package com.example.PetApp.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Applicant {

    private Long memberId;

    private String content;

}

