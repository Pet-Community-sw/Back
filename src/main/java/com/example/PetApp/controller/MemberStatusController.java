package com.example.PetApp.controller;

import com.example.PetApp.dto.member.MemberStatusDto;
import com.example.PetApp.service.member.MemberStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class MemberStatusController {
    private final MemberStatusService memberStatusService;

    @PostMapping
    private ResponseEntity<?> MemberStatus(@RequestBody MemberStatusDto statusDto) {
        return memberStatusService.updateMemberStatus(statusDto);
    }
}
