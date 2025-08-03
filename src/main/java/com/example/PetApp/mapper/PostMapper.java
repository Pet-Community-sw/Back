package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.post.Commentable;
import com.example.PetApp.domain.post.NormalPost;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.GetPostResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.PostResponseDto;
import com.example.PetApp.util.TimeAgoUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PostMapper {

    public static NormalPost toEntity(PostDto postDto, String imageFileName, Member member) {
        return NormalPost.builder()
                .content(new Content(postDto.getTitle(), postDto.getContent()))
                .postImageUrl(imageFileName)
                .member(member)
                .build();
    }

    public static <T extends Post> List<PostResponseDto> toPostListResponseDto(List<T> posts, Collection<Long> likedPostIds) {
        return posts.stream()
                .map(post -> PostResponseDto.builder()
                        .postId(post.getPostId())
                        .postImageUrl(post.getPostImageUrl())
                        .memberId(post.getMember().getMemberId())
                        .memberName(post.getMember().getName())
                        .memberImageUrl(post.getMember().getMemberImageUrl())
                        .createdAt(TimeAgoUtil.getTimeAgo(post.getCreatedAt()))
                        .viewCount(post.getViewCount())
                        .likeCount((long) post.getLikes().size())//변환
                        .title(post.getContent().getTitle())
                        .like(likedPostIds.contains(post.getPostId()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public static GetPostResponseDto toGetPostResponseDto(Post post,
                                                          Member member,
                                                          Long likeCount,
                                                          boolean isLike) {
        PostResponseDto postResponseDto=PostResponseDto.builder()
                .postId(post.getPostId())
                .title(post.getContent().getTitle())
                .postImageUrl(post.getPostImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .memberId(post.getMember().getMemberId())
                .memberName(post.getMember().getName())
                .memberImageUrl(post.getMember().getMemberImageUrl())
                .createdAt(TimeAgoUtil.getTimeAgo(post.getCreatedAt()))
                .like(isLike)
                .build();
        List<GetCommentsResponseDto> commentsResponseDtos = CommentMapper.toGetCommentsResponseDtos((Commentable)post, member);

        return GetPostResponseDto.builder()
                .content(post.getContent().getContent())
                .isOwner(post.getMember().equals(member))
                .postResponseDto(postResponseDto)
                .comments(commentsResponseDtos)
                .build();

    }

}
