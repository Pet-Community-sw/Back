package com.example.PetApp.dto.groupchat;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChatRoomList {
    private Long chatRoomId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Map<Long, Long> unReadCount;
}
