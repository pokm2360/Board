package com.example.Board_basic.Repository;

import com.example.Board_basic.Entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId, Sort sort);
    List<Comment> findByPostIdOrderByCreatedDateAsc(Long postId);
    Optional<Comment> findByIdAndPostId(Long id, Long postId); // 부모가 같은 게시글인지 검증용
}
