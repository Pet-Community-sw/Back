package com.example.PetApp.controller;

import com.example.PetApp.dto.MessageResponse;
import com.example.PetApp.dto.chatroom.CreateChatRoomResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostDto;
import com.example.PetApp.dto.walkingtogetherpost.CreateWalkingTogetherPostResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.GetWalkingTogetherPostResponseDto;
import com.example.PetApp.dto.walkingtogetherpost.UpdateWalkingTogetherPostDto;
import com.example.PetApp.service.walkingtogetherpost.WalkingTogetherPostService;
import com.example.PetApp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walking-together-posts")
public class WalkingTogetherPostController {

    private final WalkingTogetherPostService walkingTogetherPostService;


    @GetMapping("/{walkingTogetherPostId}")
    private GetWalkingTogetherPostResponseDto getWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        return walkingTogetherPostService.getWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
    }

    @GetMapping("/by-recommend-route-post/{recommendRoutePostId}")
    private List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPosts(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        return walkingTogetherPostService.getWalkingTogetherPosts(recommendRoutePostId, AuthUtil.getProfileId(authentication));
    }

    @PostMapping
    private CreateWalkingTogetherPostResponseDto createWalkingTogetherPost(@RequestBody @Valid CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Authentication authentication) {
        return walkingTogetherPostService.createWalkingTogetherPost(createWalkingTogetherPostDto, AuthUtil.getProfileId(authentication));
    }

    @PutMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> updateWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, @RequestBody @Valid UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Authentication authentication) {
        walkingTogetherPostService.updateWalkingTogetherPost(walkingTogetherPostId,
                updateWalkingTogetherPostDto,
                AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> deleteWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        walkingTogetherPostService.deleteWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @PostMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> startMatch(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        CreateChatRoomResponseDto createChatRoomResponseDto =
                walkingTogetherPostService.startMatch(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        if (createChatRoomResponseDto.isCreated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(createChatRoomResponseDto.getChatRoomId().toString()));
        } else {
            return ResponseEntity.ok(new MessageResponse("매칭 완료."));
        }
    }
}
