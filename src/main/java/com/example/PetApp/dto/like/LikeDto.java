package com.example.PetApp.dto.like;

import com.example.PetApp.service.comment.PostType;
import lombok.*;

import static com.example.PetApp.dto.commment.CommentDto.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {

    private Long postId;

    private PostType postType;

}
