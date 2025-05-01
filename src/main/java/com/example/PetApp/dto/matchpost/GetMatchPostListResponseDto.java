package com.example.PetApp.dto.matchpost;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMatchPostListResponseDto {

    private String locationName;

    private int limitCount;

    private String createdAt;

    private int currentCount;
}
