package com.example.PetApp.service.walkrecord;

import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.dto.walkrecord.CreateWalkRecordResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordLocationResponseDto;
import com.example.PetApp.dto.walkrecord.GetWalkRecordResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface WalkRecordService {
    CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost);

    GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, String email);

    void updateStartWalkRecord(Long walkRecordId, String email);

    void updateFinishWalkRecord(Long walkRecordId, String email);

    GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, String email);

}