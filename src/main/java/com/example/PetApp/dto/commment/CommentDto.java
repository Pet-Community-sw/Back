package com.example.PetApp.dto.commment;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long postId;

    private String content;

    private Long likeCount;

    private Long profileId;

}
