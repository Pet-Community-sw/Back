package com.example.PetApp.mapper;

import com.example.PetApp.domain.ChatMessage;
import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import com.example.PetApp.domain.WalkingTogetherMatch;
import com.example.PetApp.dto.groupchat.ChatMessageDto;
import com.example.PetApp.dto.groupchat.ChatRoomsResponseDto;
import com.example.PetApp.dto.groupchat.UpdateChatUnReadCountDto;
import com.example.PetApp.dto.profile.ChatRoomProfilesResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChatRoomMapper {

    public static ChatRoom toEntity(WalkingTogetherMatch walkingTogetherMatch, Profile profile) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(walkingTogetherMatch.getProfile().getPetName()+"님의 방")
                .limitCount(walkingTogetherMatch.getLimitCount())//나중에 게시물에서 인원 수를 고정.
                .walkingTogetherMatch(walkingTogetherMatch)
                //이게 수정에서 가능하려나?
                .build();
        chatRoom.addProfiles(walkingTogetherMatch.getProfile());//글 작성자.
        chatRoom.addProfiles(profile);//신청하는사람.
        return chatRoom;
    }

    public static ChatRoomsResponseDto toChatRoomsResponseDto(ChatRoom chatRoom, Long profileId, String lastMessage, int unReadCount, LocalDateTime lastMessageTime) {
        return ChatRoomsResponseDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatName(chatRoom.getName())
                .chatLimitCount(chatRoom.getLimitCount())
                .currentCount(chatRoom.getProfiles().size())
                .chatRoomTime(chatRoom.getCreatedAt())
                .profiles(
                        chatRoom.getProfiles().stream()
                                .map(profile -> ChatRoomProfilesResponseDto.builder()
                                        .profileId(profile.getProfileId())
                                        .profileImageUrl(profile.getPetImageUrl())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .lastMessage(lastMessage)
                .unReadCount(unReadCount)
                .lastMessageTime(lastMessageTime)
                .isOwner(chatRoom.getWalkingTogetherMatch().getProfile().getProfileId().equals(profileId))
                .build();
    }

    public static List<ChatMessageDto> toChatMessageDtos(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(chatMessage -> ChatMessageDto.builder()
                        .senderId(chatMessage.getSenderId())
                        .senderName(chatMessage.getSenderName())
                        .senderImageUrl(chatMessage.getSenderImageUrl())
                        .message(chatMessage.getMessage())
                        .unReadCount(chatMessage.getChatUnReadCount())
                        .messageTime(chatMessage.getMessageTime())
                        .build()
                )
                .collect(Collectors.toList());

    }

    public static UpdateChatUnReadCountDto toUpdateChatUnReadCountDto(ChatMessage chatMessage) {
        return UpdateChatUnReadCountDto.builder()
                .chatRoomId(chatMessage.getChatRoomId())
                .id(chatMessage.getId())
                .chatUnReadCount(chatMessage.getChatUnReadCount())
                .build();
    }

}
