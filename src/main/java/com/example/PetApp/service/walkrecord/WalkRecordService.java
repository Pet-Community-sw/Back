package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.DelegateWalkPost;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface WalkRecordService {
    ResponseEntity<?> createWalkRecord(DelegateWalkPost delegateWalkPost);

    ResponseEntity<?> getWalkRecord(Long walkRecordId, String email);

    ResponseEntity<?> updateStartWalkRecord(Long walkRecordId, String email);

    ResponseEntity<?> updateFinishWalkRecord(Long walkRecordId, String email);

    ResponseEntity<?> getWalkRecordLocation(Long walkRecordId, String email);

}