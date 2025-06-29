package com.example.PetApp.dto.groupchat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UpdateChatUnReadCountDto {

    private Long chatRoomId;
    private String id;
    private int chatUnReadCount;
}
