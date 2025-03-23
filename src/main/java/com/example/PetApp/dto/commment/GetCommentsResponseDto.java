package com.example.PetApp.dto.commment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCommentsResponseDto {

    private Long commentId;

    private String content;

    private Long likeCount;

    private Long profileId;

    private String profileDogName;

    private String profileImageUrl;

    private String createdAt;
}
