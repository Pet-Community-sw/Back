package com.example.PetApp.dto.post;


import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class GetPostResponseDto {

    private String content;

    private boolean isOwner;

    PostResponseDto postResponseDto;

    List<GetCommentsResponseDto> comments;

}
