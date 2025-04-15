package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.ChatRoom;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.Profile;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select size( c.profiles) from ChatRoom c where c.chatRoomId=:chatRoomId")
    int countByProfile(@Param("chatRoomId") Long chatRoomId);

    Set<ChatRoom> findAllByProfilesContains(Profile profile);// 이거 검사해봐야할듯.

    Optional<ChatRoom> findByPost(Post post);

    boolean existsByChatRoomIdAndProfilesContains(Long chatRoomId, Profile profile);

    @Modifying
    @Query("delete from ChatRoom c where c.chatRoomId=:chatRoomId")
    void deleteByChatRoom(@Param("chatRoomId") Long chatRoomId);

}
