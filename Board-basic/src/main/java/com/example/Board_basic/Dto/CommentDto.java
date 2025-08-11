package com.example.Board_basic.Dto;

import com.example.Board_basic.Entity.Comment;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long postId;
    private String content;
    private String writer;
    private String createdDate;

    public static CommentDto fromEntity(Comment c) {
        return CommentDto.builder()
                .id(c.getId())
                .postId(c.getPost().getId())
                .content(c.getContent())
                .writer(c.getWriter())
                .createdDate(c.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                .build();
    }
}
