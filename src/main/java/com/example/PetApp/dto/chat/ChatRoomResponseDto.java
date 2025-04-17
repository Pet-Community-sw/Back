package com.example.PetApp.dto.chat;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Profile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;

    private String chatName;

    private int chatLimitCount;

    private int currentCount;

    private LocalDateTime regDate;

    private Set<Long> profiles = new HashSet<>();

    private String lastMessage;

    private int unReadCount;

    private LocalDateTime lastMessageTime;



    public static ChatRoomResponseDto from(ChatRoom chatRoom, String lastMessage, int unReadCount, LocalDateTime lastMessageTime) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatName(chatRoom.getName())
                .chatLimitCount(chatRoom.getLimitCount())
                .currentCount(chatRoom.getProfiles().size())
                .regDate(chatRoom.getRegdate())
                .profiles(chatRoom.getProfiles().stream()
                        .map(Profile::getProfileId) // Profile → ID만 추출
                        .collect(Collectors.toSet()))
                .lastMessage(lastMessage)
                .unReadCount(unReadCount)
                .lastMessageTime(lastMessageTime)
                .build();
    }

}
