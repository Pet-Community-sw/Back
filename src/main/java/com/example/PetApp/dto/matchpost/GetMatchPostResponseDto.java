package com.example.PetApp.dto.matchpost;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMatchPostResponseDto {
    private Long matchPostId;

    private String content;

    private String petName;

    private String petImageUrl;

    private String locationName;

    private int currentCount;

    private int limitCount;

    private String createdAt;

    private boolean isOwner=false;

    private boolean filtering = false;

}
