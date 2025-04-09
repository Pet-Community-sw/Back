package com.example.PetApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChatRoomDto {
    private Long chatRoomId;
    private Long postId;
    private Long profileId;
    private String postTitle;
    private int limitCount;
}
