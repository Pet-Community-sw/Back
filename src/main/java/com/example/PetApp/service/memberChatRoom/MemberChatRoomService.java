package com.example.PetApp.service.memberChatRoom;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemberChatRoomService {
    ResponseEntity<?> getUserChatRooms(String email);

    ResponseEntity<?> createUserChatRoom(Long memberId, String email);

//    ResponseEntity<?> updateUserChatRoom(String userChatRoomName, String email);

    ResponseEntity<?> deleteUserChatRoom(Long userChatRoomId, String email);
}
