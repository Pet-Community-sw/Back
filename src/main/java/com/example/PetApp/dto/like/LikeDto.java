package com.example.PetApp.dto.like;

import com.example.PetApp.service.comment.PostType;
import lombok.*;

import javax.validation.constraints.NotNull;

import static com.example.PetApp.dto.commment.CommentDto.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {

    @NotNull(message = "게시물id는 필수입니다.")
    private Long postId;

    @NotNull(message = "게시글 유형은 필수입니다.")
    private PostType postType;

}
