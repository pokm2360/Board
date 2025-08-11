package com.example.Board_basic.Service;

import com.example.Board_basic.Dto.PostDto;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Entity.PostLike;
import com.example.Board_basic.Entity.PostView;
import com.example.Board_basic.Repository.CommentRepository;
import com.example.Board_basic.Repository.PostLikeRepository;
import com.example.Board_basic.Repository.PostRepository;
import com.example.Board_basic.Repository.PostViewRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostViewRepository postViewRepository;
    private final CommentRepository commentRepository;

    /**
     * ✅ 전체 게시글 페이징 조회
     */
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    /**
     * ✅ 제목 키워드로 게시글 검색 + 페이징
     */
    public Page<Post> search(String keyword, String scope, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) return postRepository.findAll(pageable);
        String s = (scope == null || scope.isBlank()) ? "all" : scope;
        return switch (s) {
            case "title" -> postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
            case "writer" -> postRepository.findByWriterContainingIgnoreCase(keyword, pageable);
            default -> postRepository.findByTitleContainingIgnoreCaseOrWriterContainingIgnoreCase(keyword, keyword, pageable);
        };
    }

    /**
     * ✅ 게시글 상세 조회 + 조회수 증가
     */
    public PostDto findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        return PostDto.fromEntity(post);
    }

    // ★ 유니크 조회수 증가: 로그인은 username 기준, 비로그인은 IP 기준 하루1회
    @Transactional
    public void increaseUniqueView(Long postId, @Nullable String username, String ip) {
        LocalDate today = LocalDate.now();
        boolean exists = (username != null)
                ? postViewRepository.existsByPostIdAndUsernameAndViewDate(postId, username, today)
                : postViewRepository.existsByPostIdAndIpAndViewDate(postId, ip, today);

        if (!exists) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
            post.setView(post.getView() + 1);
            postRepository.save(post);

            PostView pv = PostView.builder()
                    .post(post)
                    .username(username)
                    .ip(username == null ? ip : null)
                    .viewDate(today)
                    .build();
            postViewRepository.save(pv);
        }
    }

    /**
     * 좋아요 토글
     * @return true = 좋아요됨, false = 좋아요 취소됨
     */
    @Transactional
    public boolean toggleLike(Long postId, String username) {
        if (postLikeRepository.existsByPostIdAndUsername(postId, username)) {
            postLikeRepository.deleteByPostIdAndUsername(postId, username);
            return false;
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
            postLikeRepository.save(PostLike.builder().post(post).username(username).build());
            return true;
        }
    }

    //좋아요 카운트
    public long likeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    public boolean hasLiked(Long postId, String username) {
        return postLikeRepository.existsByPostIdAndUsername(postId, username);
    }
    /**
     * ✅ 게시글 작성
     */
    @Transactional
    public void save(PostDto dto) {
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .view(0)
                .createdDate(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }

    /**
     * ✅ 게시글 수정
     */
    @Transactional
    public void update(Long id, PostDto dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        post = post.toBuilder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter()) // 로그인 연동 시 수정 가능
                .view(post.getView()) // 기존 조회수 유지
                .createdDate(post.getCreatedDate()) // 기존 날짜 유지
                .build();

        postRepository.save(post);
    }

    /**
     * ✅ 게시글 삭제
     */
    @Transactional
    public void delete(Long postId) {
        // 1) 먼저 자식들 제거
        postViewRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);

        // 2) 마지막에 게시글 삭제
        postRepository.deleteById(postId);
    }

}





