package com.example.PetApp.service.chatting;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.MemberChatRoom;
import org.springframework.stereotype.Service;

@Service
public interface OfflineUserService {
    void setOfflineProfilesAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom);

    void setOfflineMembersAndUnreadCount(ChatMessage chatMessage, MemberChatRoom memberChatRoom);
}
