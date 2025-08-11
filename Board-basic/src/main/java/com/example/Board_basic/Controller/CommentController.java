package com.example.Board_basic.Controller;

import com.example.Board_basic.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public String add(@PathVariable Long postId,
                      @RequestParam String content,
                      @AuthenticationPrincipal(expression = "user.nickname") String nickname) {

        if (nickname == null || nickname.isBlank()) {
            return "redirect:/login";
        }
        commentService.add(postId, content, nickname);
        return "redirect:/posts/read/" + postId;
    }

    // 댓글 삭제
    @PostMapping("/{commentId}/delete")
    public String delete(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @AuthenticationPrincipal(expression = "user.nickname") String nickname,
                         @AuthenticationPrincipal(expression = "user.role") String role) {

        boolean isAdmin = "ROLE_ADMIN".equals(role);
        commentService.delete(commentId, nickname, isAdmin);
        return "redirect:/posts/read/" + postId;
    }
}
