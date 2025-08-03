package com.example.PetApp.repository.jpa;


import com.example.PetApp.domain.Member;
import com.example.PetApp.dto.like.LikeListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    @Query("select new com.example.PetApp.dto.like.LikeListDto(m.memberId, m.memberImageUrl, m.name) " +
            "from Member m where m.memberId in :memberIds")
    List<LikeListDto> findLikesMembers(@Param("memberIds") Collection<Long> memberIds);
}
