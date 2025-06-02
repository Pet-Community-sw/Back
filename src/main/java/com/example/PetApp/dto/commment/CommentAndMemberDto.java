package com.example.PetApp.dto.commment;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentAndMemberDto {

    private Comment comment;

    private Member member;
}
