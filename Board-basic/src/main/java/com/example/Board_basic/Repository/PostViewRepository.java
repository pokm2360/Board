package com.example.Board_basic.Repository;

import com.example.Board_basic.Entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    boolean existsByPostIdAndUsernameAndViewDate(Long postId, String username, LocalDate viewDate);
    boolean existsByPostIdAndIpAndViewDate(Long postId, String ip, LocalDate viewDate);
}
