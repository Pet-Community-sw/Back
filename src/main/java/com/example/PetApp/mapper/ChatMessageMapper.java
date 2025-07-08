package com.example.PetApp.mapper;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.dto.groupchat.UpdateChatRoomList;

import java.util.Map;

public class ChatMessageMapper {

    public static UpdateChatRoomList toUpdateChatRoomList(Long roomId, ChatMessage message, Map<Long, Long> unReadMap) {
        return UpdateChatRoomList.builder()
                .chatRoomId(roomId)
                .lastMessage(message.getMessage())
                .lastMessageTime(message.getMessageTime())
                .unReadCount(unReadMap)
                .build();
    }
}
