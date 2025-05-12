package com.example.PetApp.service.recommendroutepost;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RecommendRoutePost;
import com.example.PetApp.dto.recommendroutepost.CreateRecommendRoutePostDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendPostResponseDto;
import com.example.PetApp.dto.recommendroutepost.GetRecommendRoutePostsResponseDto;
import com.example.PetApp.dto.recommendroutepost.UpdateRecommendRoutePostDto;
import com.example.PetApp.repository.jpa.LikeRepository;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.RecommendRoutePostRepository;
import com.example.PetApp.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendRoutePostServiceImp implements RecommendRoutePostService{

    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final MemberRepository memberRepository;
    private final TimeAgoUtil timeAgoUtil;
    private final LikeRepository likeRepository;

    @Override
    public ResponseEntity<?> createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("createRecommendRoutePost 요청 memberId : {}", member.getMemberId());
        RecommendRoutePost recommendRoutePost=RecommendRoutePost.builder()
                .title(createRecommendRoutePostDto.getTitle())
                .content(createRecommendRoutePostDto.getContent())
                .locationLongitude(createRecommendRoutePostDto.getLocationLongitude())
                .locationLatitude(createRecommendRoutePostDto.getLocationLatitude())
                .member(member)
                .build();
        RecommendRoutePost saveRecommendRoutePost = recommendRoutePostRepository.save(recommendRoutePost);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("recommendRoutePostId", saveRecommendRoutePost.getRecommendRouteId()));
    }

    @Override
    public ResponseEntity<?> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getRecommendRoutePostsByLocation 요청 memberId : {}", member.getMemberId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository.findByRecommendRoutePostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01);
        List<GetRecommendRoutePostsResponseDto> list = getRecommendRoutePostsList(recommendRoutePosts,member);

        return ResponseEntity.ok(list);

    }

    @Override
    public ResponseEntity<?> getRecommendRoutePosts(Double longitude, Double latitude, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getRecommendRoutePostsByPlace 요청 memberId : {}", member.getMemberId());
        List<RecommendRoutePost> recommendRoutePosts = recommendRoutePostRepository.findByRecommendRoutePostByPlace(longitude, latitude);
        List<GetRecommendRoutePostsResponseDto> list = getRecommendRoutePostsList(recommendRoutePosts,member);

        return ResponseEntity.ok(list);

    }

    @Override
    public ResponseEntity<?> getRecommendRoutePost(Long recommendRoutePostId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("getRecommendRoutePost 요청 memberId : {}", member.getMemberId());
        Optional<RecommendRoutePost> post = recommendRoutePostRepository.findById(recommendRoutePostId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책길 추천 게시물은 없습니다.");
        }
        GetRecommendPostResponseDto getRecommendPostResponseDto=GetRecommendPostResponseDto.builder()
                .recommendRoutePostId(post.get().getRecommendRouteId())
                .title(post.get().getTitle())
                .content(post.get().getContent())
                .memberId(post.get().getMember().getMemberId())
                .memberName(post.get().getMember().getName())
                .memberImageUrl(post.get().getMember().getMemberImageUrl())
                .createdAt(timeAgoUtil.getTimeAgo(post.get().getRecommendRouteTime()))
                .likeCount(likeRepository.countByRecommendRoutePost(post.get()))
                .isOwner(post.get().getMember().getMemberId().equals(member.getMemberId()))
                .isLiked(likeRepository.existsByRecommendRoutePostAndMember(post.get(), member))
                .build();
        return ResponseEntity.ok(getRecommendPostResponseDto);

    }

    @Override
    public ResponseEntity<?> updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("updateRecommendRoutePost 요청 memberId : {}", member.getMemberId());
        Optional<RecommendRoutePost> post = recommendRoutePostRepository.findById(recommendRoutePostId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책길 추천 게시물은 없습니다.");
        } else if (post.get().getMember().equals(member)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }
        post.get().setTitle(updateRecommendRoutePostDto.getTitle());
        post.get().setContent(updateRecommendRoutePostDto.getContent());

        return ResponseEntity.ok().body("수정 완료.");
    }

    @Override
    public ResponseEntity<?> deleteRecommendRoutePost(Long recommendRoutePostId, String email) {
        Member member = memberRepository.findByEmail(email).get();
        log.info("deleteRecommendRoutePost 요청 memberId : {}", member.getMemberId());
        Optional<RecommendRoutePost> post = recommendRoutePostRepository.findById(recommendRoutePostId);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 산책길 추천 게시물은 없습니다.");
        } else if (post.get().getMember().equals(member)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
        recommendRoutePostRepository.deleteById(recommendRoutePostId);
        return ResponseEntity.ok().body("삭제 완료.");
    }

    @NotNull
    private List<GetRecommendRoutePostsResponseDto> getRecommendRoutePostsList(List<RecommendRoutePost> recommendRoutePosts, Member member) {
        return recommendRoutePosts.stream()
                .map(recommendRoutePost -> new GetRecommendRoutePostsResponseDto(
                        recommendRoutePost.getRecommendRouteId(),
                        recommendRoutePost.getTitle(),
                        recommendRoutePost.getMember().getMemberId(),
                        recommendRoutePost.getMember().getName(),
                        recommendRoutePost.getMember().getMemberImageUrl(),
                        likeRepository.countByRecommendRoutePost(recommendRoutePost),
                        recommendRoutePost.getLocationLongitude(),
                        recommendRoutePost.getLocationLatitude(),
                        timeAgoUtil.getTimeAgo(recommendRoutePost.getRecommendRouteTime()),
                        member.getMemberId().equals(recommendRoutePost.getMember().getMemberId()),
                        likeRepository.existsByRecommendRoutePostAndMember(recommendRoutePost, member)
                )).collect(Collectors.toList());
    }
}
