package com.example.PetApp.dto.delegateWalkpost;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GetDelegateWalkPostsResponseDto {

    private Long delegateWalkPostId;

    private Long profileId;

    private String petName;

    private String petImageUrl;

    private String title;

    private Long price;

    private Double locationLongitude;

    private Double locationLatitude;

    private LocalDateTime scheduledTime;

    private String createdAt;

    private int applicantCount;

    private boolean filtering;
}
