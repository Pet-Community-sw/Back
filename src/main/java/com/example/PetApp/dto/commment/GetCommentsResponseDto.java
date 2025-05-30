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

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    private String createdAt;

    private boolean isOwner;

}
