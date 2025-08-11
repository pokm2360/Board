package com.example.Board_basic.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(columnList = "post_id,viewDate"),
        @Index(columnList = "username,viewDate"),
        @Index(columnList = "ip,viewDate")
})
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    private String username;        // 로그인 시
    private String ip;              // 비로그인 시

    @Column(nullable = false)
    private LocalDate viewDate;     // ★ 날짜 단위 유니크
}
