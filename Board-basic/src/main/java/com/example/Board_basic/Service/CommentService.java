package com.example.Board_basic.Service;

import com.example.Board_basic.Dto.CommentDto;
import com.example.Board_basic.Entity.Comment;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Repository.CommentRepository;
import com.example.Board_basic.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<CommentDto> listByPost(Long postId) {
        return commentRepository.findByPostId(postId, Sort.by(Sort.Direction.DESC, "id"))
                .stream().map(CommentDto::fromEntity).toList();
    }

    public Long add(Long postId, String content, String nickname) {
        if (nickname == null || nickname.isBlank())
            throw new IllegalStateException("로그인이 필요합니다.");
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("댓글 내용을 입력하세요.");

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment saved = commentRepository.save(
                Comment.builder()
                        .post(post)
                        .content(content)
                        .writer(nickname)
                        .createdDate(LocalDateTime.now())
                        .build()
        );
        return saved.getId();
    }

    public void delete(Long commentId, String nickname, boolean isAdmin) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        if (!isAdmin && !c.getWriter().equals(nickname)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(c);
    }
}
