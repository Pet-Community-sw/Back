package com.example.PetApp.mapper;

import com.example.PetApp.domain.Comment;
import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.commment.CommentDto;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.util.TimeAgoUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static List<GetCommentsResponseDto> toGetCommentsResponseDtos(Post post, Member member) {
        return getCommentsResponseDtos(post.getComments(), member);
    }

    public static List<GetCommentsResponseDto> toGetCommentsResponseDtos(RecommendRoutePost recommendRoutePost, Member member) {
        return getCommentsResponseDtos(recommendRoutePost.getComments(), member);
    }

    public static Comment toEntity(CommentDto commentDto, Post post, Member member) {
        return Comment.builder()
                .content(commentDto.getContent())
                .post(post)
                .member(member)
                .build();
    }

    public static Comment toEntity(CommentDto commentDto,RecommendRoutePost recommendRoutePost, Member member) {
        return Comment.builder()
                .content(commentDto.getContent())
                .recommendRoutePost(recommendRoutePost)
                .member(member)
                .build();
    }

    @NotNull
    private static List<GetCommentsResponseDto> getCommentsResponseDtos(List<Comment> comments, Member member) {
        return comments.stream().map(
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
