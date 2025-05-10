package com.example.PetApp.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberStatusDto {

    public enum StatusType{
        ONLINE, OFFLINE
    }

    private Long memberId;

    private StatusType statusType;

}
