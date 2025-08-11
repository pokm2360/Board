package com.example.Board_basic.Repository;

import com.example.Board_basic.Entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    boolean existsByPostIdAndUsernameAndViewDate(Long postId, String username, LocalDate viewDate);
    boolean existsByPostIdAndIpAndViewDate(Long postId, String ip, LocalDate viewDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PostView v where v.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

}
