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

    private Long postId;

    private Long memberId;

    private String content;

}
