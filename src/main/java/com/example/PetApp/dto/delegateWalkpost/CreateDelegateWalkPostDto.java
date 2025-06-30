package com.example.PetApp.dto.delegateWalkpost;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Builder
public class CreateDelegateWalkPostDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "가격은 필수입니다.")
    private Long price;

    @NotNull(message = "위치경도는 필수입니다.")
    private Double locationLongitude;

    @NotNull(message = "위치위도는 필수입니다.")
    private Double locationLatitude;

    @NotNull(message = "산책 범위는 필수입니다.")
    private Integer allowedRadiusMeters;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;

    @NotNull(message = "프로필 여부는 필수입니다.")
    private boolean requireProfile;

}
