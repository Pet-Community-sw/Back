package com.example.PetApp.repository;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findByMemberId(Long memberId);

    Long countByMemberId(Long memberId);
}
