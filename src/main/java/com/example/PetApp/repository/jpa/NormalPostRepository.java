package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.post.NormalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NormalPostRepository extends JpaRepository<NormalPost, Long> {

    @Query("select n from NormalPost n left join fetch n.likes")
    List<NormalPost> findAllWithLikes();
}
