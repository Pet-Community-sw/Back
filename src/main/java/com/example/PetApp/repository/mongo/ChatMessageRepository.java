package com.example.PetApp.repository.mongo;


import com.example.PetApp.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import static com.example.PetApp.domain.ChatMessage.*;


@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    void deleteByChatRoomId(Long chatRoomId);

    Page<ChatMessage> findAllByChatRoomIdAndChatRoomType(Long chatRoomId, ChatRoomType chatRoomType, Pageable pageable);
}
