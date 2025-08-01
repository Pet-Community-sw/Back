package com.example.PetApp.domain.post;

import com.example.PetApp.domain.Comment;

import java.util.List;

public interface Commentable {
    List<Comment> getComments();
}
