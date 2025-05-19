package com.example.PetApp.controller;

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
    private final AuthUtil authUtil;

    @GetMapping("/{walkRecordId}")
    public ResponseEntity<?> getWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordService.getWalkRecord(walkRecordId, authUtil.getEmail(authentication));
    }

    @GetMapping("/{walkRecordId}/location")
    public ResponseEntity<?> getWalkRecordLocation(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordService.getWalkRecordLocation(walkRecordId, authUtil.getEmail(authentication));
    }

    @PutMapping("/{walkRecordId}/start")
    public ResponseEntity<?> updateStartWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordService.updateStartWalkRecord(walkRecordId, authUtil.getEmail(authentication));
    }

    @PutMapping("/{walkRecordId}/finish")
    public ResponseEntity<?> updateFinishWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordService.updateFinishWalkRecord(walkRecordId, authUtil.getEmail(authentication));
    }
}
