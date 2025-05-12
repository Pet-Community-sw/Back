//package com.example.PetApp.controller;
//
//import com.example.PetApp.service.memberChatRoom.MemberChatRoomService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/member-chat-rooms")
//public class MemberChatRoomController {
//    private final MemberChatRoomService memberChatRoomService;
//
//    @GetMapping
//    public ResponseEntity<?> getUserChatRooms(Authentication authentication) {
//        String email = authentication.getPrincipal().toString();
//        return memberChatRoomService.getUserChatRooms(email);
//    }
//
//    @PostMapping
//    public ResponseEntity<?> createUserChatRoom(@RequestBody Long memberId, Authentication authentication) {
//        String email = authentication.getPrincipal().toString();
//        return memberChatRoomService.createUserChatRoom(memberId, email);
//    }
//
////    @PutMapping
////    public ResponseEntity<?> updateUserChatRoom(@RequestBody String memberChatRoomName, Authentication authentication) {
////        String email = authentication.getPrincipal().toString();
////        return memberChatRoomService.updateUserChatRoom(memberChatRoomName, email);
////    }
//
//    @DeleteMapping("/{memberChatRoomId}")
//    public ResponseEntity<?> deleteUserChatRoom(@PathVariable Long memberChatRoomId, Authentication authentication) {
//        String email = authentication.getPrincipal().toString();
//        return memberChatRoomService.deleteUserChatRoom(memberChatRoomId, email);
//    }
//}
