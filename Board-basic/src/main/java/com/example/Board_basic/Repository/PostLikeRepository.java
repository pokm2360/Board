package com.example.Board_basic.Repository;

import com.example.Board_basic.Entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUsername(Long postId, String username);
    void deleteByPostIdAndUsername(Long postId, String username);
    long countByPostId(Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PostLike l where l.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

}
