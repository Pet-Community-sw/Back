package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.LikeT;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeT, Long> {

    Long countByPost(Post post);

    Boolean existsByPostAndMember(Post post, Member member);

    void deleteByPostAndMember(Post post, Member member);
}
