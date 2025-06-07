package com.example.PetApp.controller;

import com.example.PetApp.dto.MessageResponse;
import com.example.PetApp.service.member.MemberService;
import com.example.PetApp.service.member.MemberStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class MemberStatusController {

    private final MemberStatusService memberStatusService;

    @PostMapping
    private ResponseEntity<MessageResponse> MemberStatus(Authentication authentication) {
        memberStatusService.updateMemberStatus(authentication.getPrincipal().toString());
        return ResponseEntity.ok(new MessageResponse("foreGround"));
    }
}
