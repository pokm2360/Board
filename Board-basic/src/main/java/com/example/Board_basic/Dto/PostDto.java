package com.example.Board_basic.Dto;

import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        private String title;
        private String content;

        public Post toEntity() {
            return Post.builder()
                    .title(this.title)
                    .content(this.content)
                    .build();
        }
    }

    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String writer;
        private Long userId;
        private List<?> comments;

        public Response(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.writer = post.getWriter();
            this.userId = post.getUser() != null ? post.getUser().getId() : null;
            this.comments = post.getComments() != null ?
                    post.getComments().stream().map(CommentDto.Response::new).collect(Collectors.toList())
                    : Collections.emptyList().reversed();
        }
    }
}
