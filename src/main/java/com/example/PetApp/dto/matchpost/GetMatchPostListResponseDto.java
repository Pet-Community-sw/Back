package com.example.PetApp.dto.matchpost;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMatchPostListResponseDto {
    private Long matchPostId;

    private String locationName;

    private int limitCount;

    private String createdAt;

    private int currentCount;

    private Double longitude;

    private Double latitude;
}
