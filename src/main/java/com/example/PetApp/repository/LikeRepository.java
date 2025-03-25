package com.example.PetApp.repository;

import com.example.PetApp.domain.LikeT;
import com.example.PetApp.dto.like.LikeResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeT, Long> {


    Long countByPostId(Long postId);

    Optional<LikeT> findByProfileId(Long profileId);

    void deleteByPostIdAndProfileProfileId(Long postId, Long profileId);
}
