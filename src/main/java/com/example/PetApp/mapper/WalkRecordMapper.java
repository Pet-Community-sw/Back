package com.example.PetApp.mapper;

import com.example.PetApp.domain.DelegateWalkPost;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.WalkRecord;

public class WalkRecordMapper {

    public static WalkRecord toEntity(DelegateWalkPost delegateWalkPost, Member member) {
        return WalkRecord.builder()
                .walkStatus(WalkRecord.WalkStatus.READY)
                .delegateWalkPost(delegateWalkPost)
                .member(member)
                .build();
    }
}
