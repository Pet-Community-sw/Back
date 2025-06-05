package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.dto.walkrecord.CreateWalkRecordResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface WalkRecordService {
    CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost);

    ResponseEntity<?> getWalkRecord(Long walkRecordId, String email);

    ResponseEntity<?> updateStartWalkRecord(Long walkRecordId, String email);

    ResponseEntity<?> updateFinishWalkRecord(Long walkRecordId, String email);

    ResponseEntity<?> getWalkRecordLocation(Long walkRecordId, String email);

}