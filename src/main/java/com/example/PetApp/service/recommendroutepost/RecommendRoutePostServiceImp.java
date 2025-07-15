package com.example.PetApp.service.recommendroutepost;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.domain.embedded.PostContent;
import com.example.PetApp.dto.like.LikeCountDto;
import com.example.PetApp.dto.recommendroutepost.*;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.mapper.RecommendRoutePostMapper;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendRoutePostServiceImp implements RecommendRoutePostService{

    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    @Transactional
    @Override
    public CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, String email) {
        log.info("createRecommendRoutePost 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        RecommendRoutePost recommendRoutePost = RecommendRoutePostMapper.toEntity(createRecommendRoutePostDto, member);
        RecommendRoutePost savedRecommendRoutePost = recommendRoutePostRepository.save(recommendRoutePost);
        return new CreateRecommendRoutePostResponseDto(savedRecommendRoutePost.getRecommendRouteId());
    }

    @Transactional(readOnly = true)//페이징 처리를 해야됨. 40개 정도 내보내면 프론트가 페이지 처리할 수 있으려나?
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email) {
        log.info("getRecommendRoutePostsByLocation 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        Pageable pageable = PageRequest.of(page, 10);
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository
                .findByRecommendRoutePostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01, pageable)
                .getContent();
        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts,
                getLikeCountMap(recommendRoutePosts),
                likeRepository.findLikedRecommendIds(member, recommendRoutePosts),
                member);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, String email) {
        log.info("getRecommendRoutePostsByPlace 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        Pageable pageable = PageRequest.of(page, 10);
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository.findByRecommendRoutePostByPlace(longitude, latitude, pageable).getContent();

        return RecommendRoutePostMapper.toRecommendRoutePostsList(recommendRoutePosts,
                getLikeCountMap(recommendRoutePosts),
                likeRepository.findLikedRecommendIds(member, recommendRoutePosts),
                member);
    }

    @Transactional(readOnly = true)
    @Override
    public GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, String email) {
        log.info("getRecommendRoutePost 요청 email : {}", email);
        Member member = memberRepository.findByEmail(email).get();
        RecommendRoutePost recommendRoutePost = recommendRoutePostRepository.findById(recommendRoutePostId)
                .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시물은 없습니다."));
        return RecommendRoutePostMapper.toGetRecommendPostResponseDto(member,
                recommendRoutePost,
                likeRepository.countByRecommendRoutePost(recommendRoutePost),
                likeRepository.existsByRecommendRoutePostAndMember(recommendRoutePost, member)
        );

    }

    @Transactional
    @Override
    public void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, String email) {
        log.info("updateRecommendRoutePost 요청 recommendRoutePostId : {}, email : {}", recommendRoutePostId, email);
        Member member = memberRepository.findByEmail(email).get();
        RecommendRoutePost post = recommendRoutePostRepository.findById(recommendRoutePostId)
                .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
        if (!(post.getMember().equals(member))) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }
        post.setPostContent(new PostContent(updateRecommendRoutePostDto.getTitle(), updateRecommendRoutePostDto.getContent()));
    }

    @Transactional
    @Override
    public void deleteRecommendRoutePost(Long recommendRoutePostId, String email) {
        log.info("deleteRecommendRoutePost 요청 recommendRoutePostId : {}, email : {}", recommendRoutePostId, email);
        Member member = memberRepository.findByEmail(email).get();
        RecommendRoutePost post = recommendRoutePostRepository.findById(recommendRoutePostId)
                .orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
        if (!(post.getMember().equals(member))) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }
        recommendRoutePostRepository.deleteById(recommendRoutePostId);
    }

    public Map<Long, Long> getLikeCountMap(List<RecommendRoutePost> recommendRoutePosts) {
        List<LikeCountDto> likeCountDtos = likeRepository.countByRecommendRoutePost(recommendRoutePosts);
        return likeCountDtos.stream().collect(Collectors.toMap(
                LikeCountDto::getPostId,
                LikeCountDto::getLikeCount
        ));
    }

}
