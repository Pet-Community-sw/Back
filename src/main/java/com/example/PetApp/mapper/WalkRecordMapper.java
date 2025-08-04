package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.dto.walkrecord.GetWalkRecordResponseDto;
import com.example.PetApp.util.CreatedAtUtil;

public class WalkRecordMapper {

    public static WalkRecord toEntity(DelegateWalkPost delegateWalkPost, Member member) {
        return WalkRecord.builder()
                .walkStatus(WalkRecord.WalkStatus.READY)
                .delegateWalkPost(delegateWalkPost)
                .member(member)
                .build();
    }

    public static GetWalkRecordResponseDto toGetWalkRecordResponseDto(WalkRecord walkRecord) {
        return GetWalkRecordResponseDto.builder()
                .walkRecordId(walkRecord.getWalkRecordId())
                .startTime(walkRecord.getStartTime())
                .finishTime(walkRecord.getFinishTime())
                .walkTime(CreatedAtUtil.createdAt(walkRecord.getStartTime(),walkRecord.getFinishTime()))
                .walkDistance(walkRecord.getWalkDistance())
                .pathPoints(walkRecord.getPathPoints())
                .build();
    }
}
