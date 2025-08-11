package com.example.Board_basic.Service;

import com.example.Board_basic.Dto.PostDto;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * ✅ 전체 게시글 페이징 조회
     */
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    /**
     * ✅ 제목 키워드로 게시글 검색 + 페이징
     */
    public Page<Post> search(String keyword, Pageable pageable) {
        return postRepository.findByTitleContaining(keyword, pageable);
    }

    /**
     * ✅ 게시글 상세 조회 + 조회수 증가
     */
    public PostDto findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        post.setView(post.getView() + 1); // 조회수 증가
        postRepository.save(post);
        return PostDto.fromEntity(post);
    }

    /**
     * ✅ 게시글 작성
     */
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
    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}





