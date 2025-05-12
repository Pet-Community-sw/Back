package com.example.PetApp.dto.like;

import lombok.*;

import static com.example.PetApp.dto.commment.CommentDto.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {

    private Long postId;

    private PostType postType;

}
