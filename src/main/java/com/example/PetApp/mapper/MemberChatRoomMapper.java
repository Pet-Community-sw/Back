package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberChatRoom;
import com.example.PetApp.dto.memberchat.MemberChatRoomsResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MemberChatRoomMapper {

    public static MemberChatRoomsResponseDto toMemberChatRoomsResponseDto(String roomName, String roomImageUrl, String lastMessage, String count, String lastMessageTime) {
        int unReadCount = count != null ? Integer.parseInt(count) : 0;
        LocalDateTime lastMessageLocalDateTime = null;
        if (lastMessageTime != null) {
            lastMessageLocalDateTime = LocalDateTime.parse(lastMessageTime);
        }
        return MemberChatRoomsResponseDto.builder()
                .chatName(roomName)
                .chatImageUrl(roomImageUrl)
                .lastMessage(lastMessage)
                .unReadCount(unReadCount)
                .lastMessageTime(lastMessageLocalDateTime)
                .build();
    }


}
