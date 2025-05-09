package com.example.PetApp.dto.groupchat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatMessageDto {

    private Long senderId;

    private String senderName;

    private String senderImageUrl;

    private String message;

    private int unReadCount;

    private LocalDateTime messageTime;

}
