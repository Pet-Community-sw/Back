package com.example.PetApp.dto.post;


import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUpdatePostResponseDto {

    private Long postId;

    private String title;

    private String content;

    private String postImageUrl;

    private Long viewCount;

    private Long likeCount;

    private Long profileId;

    private String profileName;

    private String profileImageUrl;

    private boolean isOwner;

    List<GetCommentsResponseDto> comments;

}
