package com.example.PetApp.dto.groupchat;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {

    private Long chatRoomId;

    private List<ChatMessageDto> messages;
}
