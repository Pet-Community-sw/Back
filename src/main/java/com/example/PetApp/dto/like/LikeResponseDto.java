package com.example.PetApp.dto.like;

import com.example.PetApp.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class LikeResponseDto {
    private List<Member> members=new ArrayList<>();
    private Long likeCount;

}
