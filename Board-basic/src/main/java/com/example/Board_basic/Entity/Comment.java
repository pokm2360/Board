package com.example.Board_basic.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Builder(toBuilder = true)
@NoArgsConstructor @AllArgsConstructor
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, length = 100)
    private String writer;          // 작성자 닉네임

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;
}


