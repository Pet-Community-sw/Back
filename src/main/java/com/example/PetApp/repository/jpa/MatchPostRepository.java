package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.WalkingTogetherPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchPostRepository extends JpaRepository<WalkingTogetherPost, Long> {
}
