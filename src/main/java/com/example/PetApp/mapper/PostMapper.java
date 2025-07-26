package com.example.PetApp.mapper;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.Post;
import com.example.PetApp.domain.embedded.Content;
import com.example.PetApp.dto.commment.GetCommentsResponseDto;
import com.example.PetApp.dto.post.GetPostResponseDto;
import com.example.PetApp.dto.post.PostDto;
import com.example.PetApp.dto.post.PostResponseDto;
import com.example.PetApp.util.TimeAgoUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PostMapper {

    public static Post toEntity(PostDto postDto, String imageFileName, Member member) {
        return Post.builder()
                .content(new Content(postDto.getTitle(), postDto.getContent()))
                .postImageUrl(imageFileName)
                .member(member)
                .build();
    }

    public static List<PostResponseDto> toPostListResponseDto(List<Post> posts,
                                                              Map<Long, Long> likeCountMap,
                                                              Set<Long> likedPostIds) {
        return posts.stream()
                .map(post -> PostResponseDto.builder()
                        .postId(post.getPostId())
                        .postImageUrl(post.getPostImageUrl())
                        .memberId(post.getMember().getMemberId())
                        .memberName(post.getMember().getName())
                        .memberImageUrl(post.getMember().getMemberImageUrl())
                        .createdAt(TimeAgoUtil.getTimeAgo(post.getCreatedAt()))
                        .viewCount(post.getViewCount())
                        .likeCount(likeCountMap.get(post.getPostId()))
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
        List<GetCommentsResponseDto> commentsResponseDtos = CommentMapper.toGetCommentsResponseDtos(post, member);

        return GetPostResponseDto.builder()
                .content(post.getContent().getContent())
                .isOwner(post.getMember().equals(member))
                .postResponseDto(postResponseDto)
                .comments(commentsResponseDtos)
                .build();

    }

}
