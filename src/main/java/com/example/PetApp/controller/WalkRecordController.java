package com.example.PetApp.controller;

import com.example.PetApp.dto.MessageResponse;
import com.example.PetApp.dto.walkrecord.GetWalkRecordLocationResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordResponseDto;
import com.example.PetApp.service.walkrecord.WalkRecordService;
import com.example.PetApp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walk-record")//삭제도 있어야하나?
public class WalkRecordController {
    private final WalkRecordService walkRecordService;

    @GetMapping("/{walkRecordId}")
    public GetWalkRecordResponseDto getWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordService.getWalkRecord(walkRecordId, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{walkRecordId}/location")
    public GetWalkRecordLocationResponseDto getWalkRecordLocation(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordService.getWalkRecordLocation(walkRecordId, AuthUtil.getEmail(authentication));
    }

    @PutMapping("/{walkRecordId}/start")
    public ResponseEntity<MessageResponse> updateStartWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        walkRecordService.updateStartWalkRecord(walkRecordId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("start"));
    }

    @PutMapping("/{walkRecordId}/finish")
    public ResponseEntity<MessageResponse> updateFinishWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        walkRecordService.updateFinishWalkRecord(walkRecordId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("finish"));
    }
}
