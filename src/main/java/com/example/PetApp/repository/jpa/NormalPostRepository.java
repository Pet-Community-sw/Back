package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.post.NormalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalPostRepository extends JpaRepository<NormalPost, Long> {

}
