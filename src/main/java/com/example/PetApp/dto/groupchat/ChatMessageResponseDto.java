package com.example.PetApp.dto.groupchat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long chatRoomId;

    private List<ChatMessageDto> messages;
}
