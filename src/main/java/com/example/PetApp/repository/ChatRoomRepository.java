package com.example.PetApp.repository;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select c.profiles.size from ChatRoom c")
    int countByProfile(Long chatRoomId);

    Set<ChatRoom> findAllByProfilesContains(Profile profile);// 이거 검사해봐야할듯.

    Optional<ChatRoom> findByPost(Post post);
}
