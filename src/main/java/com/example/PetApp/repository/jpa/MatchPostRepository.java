package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.MatchPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchPostRepository extends JpaRepository<MatchPost, Long> {

}
