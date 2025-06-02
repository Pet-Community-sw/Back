package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.util.TimeAgoUtil;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static List<GetCommentsResponseDto> getCommentsResponseDtos(Post post, Member member) {
        return post.getComments().stream().map(
                comment -> new GetCommentsResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getMember().getMemberId(),
                        comment.getMember().getName(),
                        comment.getMember().getMemberImageUrl(),
                        TimeAgoUtil.getTimeAgo(comment.getCommentTime()),
                        comment.getMember().equals(member)
                )
        ).collect(Collectors.toList());
    }
}
