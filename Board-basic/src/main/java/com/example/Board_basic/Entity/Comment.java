package com.example.Board_basic.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Builder.Default;
// ... 기타 import

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Default
    private List<Comment> children = new ArrayList<>();  // ✅ 빌더 기본값 유지

    private int depth;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, length = 50)
    private String writer;

    private LocalDateTime createdDate;

    // 선택: 편의 메서드
    public void addChild(Comment child) {
        this.children.add(child);
        child.parent = this;
        child.depth = this.depth + 1;
        child.post = this.post; // 필요 시
    }
}


