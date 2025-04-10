package com.example.PetApp.dto.chat;

import com.example.PetApp.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class ChatMessageDto {

    private Long chatRoomId;

    private List<ChatMessage> messageList;
}
