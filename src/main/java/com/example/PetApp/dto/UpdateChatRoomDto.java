package com.example.PetApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateChatRoomDto {

    private Long chatRoomId;

    private Long profileId;

    private String chatRoomTitle;

    private int limitCount;
}
