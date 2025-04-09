package com.example.PetApp.dto;

import com.example.PetApp.domain.ChatRoom;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private ChatRoom chatRoom;

    private int profileCount;

    private int unReadCount;

    private String lastMessage;

    private LocalDateTime lastMessageTime;
}
