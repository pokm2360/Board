package com.example.Board_basic.Repository;

import com.example.Board_basic.Entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUsername(Long postId, String username);
    void deleteByPostIdAndUsername(Long postId, String username);
    long countByPostId(Long postId);
}
