package com.example.PetApp.dto.commment;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    @NotNull(message = "게시물 id는 필수입니다.")
    private Long postId;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

}
