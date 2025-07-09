package com.example.PetApp.service.chatting.handler;

import com.example.PetApp.domain.*;
import com.example.PetApp.repository.jpa.ChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberChatRoomRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.ProfileRepository;
import com.example.PetApp.service.chatting.OfflineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomHandlerImp implements ChatRoomHandler{
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final OfflineUserService offlineUserService;

    @Override
    public void handleGroupChat(ChatMessage chatMessage, Long senderId) {
        Profile profile = profileRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        chatMessage.setSenderImageUrl(profile.getPetImageUrl());
        chatMessage.setSenderName(profile.getPetName());

        offlineUserService.setOfflineProfilesAndUnreadCount(chatMessage, chatRoom);
    }

    @Override
    public void handleOneToOneChat(ChatMessage chatMessage, Long senderId) {
        Member member = memberRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(chatMessage.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        chatMessage.setSenderImageUrl(member.getMemberImageUrl());
        chatMessage.setSenderName(member.getName());

        offlineUserService.setOfflineMembersAndUnreadCount(chatMessage, memberChatRoom);
    }
}
