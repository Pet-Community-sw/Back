package com.example.PetApp.service.walkerpost;

import com.example.PetApp.domain.Applicant;
import com.example.PetApp.dto.delegateWalkpost.*;
import com.example.PetApp.dto.memberchat.CreateMemberChatRoomResponseDto;
import com.example.PetApp.dto.walkrecord.CreateWalkRecordResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface DelegateWalkPostService {
    CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId);

    ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, String email);

    GetPostResponseDto getDelegateWalkPost(Long delegateWalkPostId, String email);

    void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email);

    void deleteDelegateWalkPost(Long delegateWalkPostId, String email);

    Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId);

    ResponseEntity<?> checkProfile(Long profileId);

    CreateMemberChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, String email);

    CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId);
}
