package com.example.Board_basic.Controller;

import com.example.Board_basic.Dto.CommentDto;
import com.example.Board_basic.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDto.Request dto) {
        commentService.save(dto);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentDto.Request dto) {
        dto.setId(id);
        commentService.update(dto);
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}

