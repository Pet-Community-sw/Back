package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberChatRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    @Query("select case when count (mc)>0 then  true else false end " +
            "from MemberChatRoom mc where :member1 member of mc.members and :member2 member of mc.members")
    boolean existsByMembers(@Param("member1") Member member1, @Param("member2") Member member2);

    List<MemberChatRoom> findAllByMembersContains(Member member);
}
