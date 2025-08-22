package com.example.PetApp.dto.delegateWalkpost;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostResponseDto { //후기 추가해야됨.

    private Long delegateWalkPostId;

    private String title;

    private String content;

    private Long price;

    private Double locationLongitude;

    private Double locationLatitude;

    private Integer allowedRadiusMeters;

    private LocalDateTime scheduledTime;

    private String petName;

    private String petImageUrl;

    private String petBreed;

    private String extraInfo;

    private String createdAt;

    private int applicantCount;

}
