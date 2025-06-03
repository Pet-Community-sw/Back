package com.example.PetApp.dto.chatroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateChatRoomResponseDto {

    private Long chatRoomId;

    private boolean isCreated;
}
