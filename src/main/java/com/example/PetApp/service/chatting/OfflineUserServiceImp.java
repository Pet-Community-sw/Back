package com.example.PetApp.service.chatting;

import com.example.PetApp.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfflineUserServiceImp implements OfflineUserService{

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void setOfflineProfilesAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom) {
        List<Profile> profiles = chatRoom.getProfiles();
        Set<String> onlineProfiles = stringRedisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoom.getChatRoomId() + ":onlineProfiles");
        List<Long> offlineProfiles = profiles.stream()
                .map(Profile::getProfileId)
                .filter(profileId -> onlineProfiles == null || !onlineProfiles.contains(profileId.toString()))
                .collect(Collectors.toList());

        chatMessage.setUsers(offlineProfiles);
        chatMessage.setChatUnReadCount(offlineProfiles.size());

        log.info("오프라인 프로필 목록 설정 (MANY): {}", offlineProfiles);
    }

    @Override
    public void setOfflineMembersAndUnreadCount(ChatMessage chatMessage, MemberChatRoom memberChatRoom) {
        List<Member> members = memberChatRoom.getMembers();
        Set<String> onlineMembers = stringRedisTemplate.opsForSet()
                .members("memberChatRoomId:" + memberChatRoom.getMemberChatRoomId() + ":onlineMembers");
        List<Long> offlineMembers = members.stream()
                .map(Member::getMemberId)
                .filter(memberId -> onlineMembers == null || !onlineMembers.contains(memberId.toString()))
                .collect(Collectors.toList());

        chatMessage.setUsers(offlineMembers);
        chatMessage.setChatUnReadCount(offlineMembers.size());

        log.info("오프라인 멤버 목록 설정 (ONE): {}", offlineMembers);
    }
}
