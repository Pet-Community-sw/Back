package com.example.PetApp.dto.commment;


import com.example.PetApp.service.comment.PostType;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
public class CommentDto {

    private Long postId;

    private String content;

    private PostType postType;

}
