package com.example.PetApp.service.chatting.handler;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.service.chatting.handler.ChatRoomHandlerImp.ChatRoomAccess;
import com.example.PetApp.service.chatting.handler.ChatRoomHandlerImp.MemberChatRoomAccess;
import org.springframework.stereotype.Service;

@Service
public interface ChatRoomHandler {
    void handleGroupChat(ChatMessage chatMessage, Long senderId);

    void handleOneToOneChat(ChatMessage chatMessage, Long senderId);

    ChatRoomAccess verifyChatRoomAccess(Long chatRoomId, Long senderId);

    MemberChatRoomAccess verifyMemberChatRoomAccess(Long chatRoomId, Long senderId);
}
