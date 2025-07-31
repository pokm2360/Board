package com.example.Board_basic.Dto;

import com.example.Board_basic.Entity.Comment;
import lombok.Getter;
import lombok.Setter;

public class CommentDto {

    @Getter
    @Setter
    public static class Request {
        private Long id;
        private String comment;
        private Long postId;
        private Long userId;

        public Comment toEntity() {
            return Comment.builder()
                    .id(id)
                    .comment(comment)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class Response {
        private Long id;
        private String comment;
        private String createdDate;
        private String writer;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.comment = comment.getComment();
            this.createdDate = comment.getCreatedDate();
            this.writer = comment.getUser() != null ? comment.getUser().getUsername() : null;
        }
    }
}

