package com.example.PetApp.dto.commment;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NotBlank
public class CommentDto {
    public enum PostType {
        COMMUNITY,RECOMMEND
    }

    private Long postId;

    private String content;

    private PostType postType;

}
