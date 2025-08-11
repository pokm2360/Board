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

    /** 원댓글 등록 */
    @PostMapping
    public String add(@PathVariable("postId") Long postId,
                      @RequestParam("content") String content,
                      @AuthenticationPrincipal(expression = "user.nickname") String nickname) {

        if (nickname == null || nickname.isBlank()) {
            return "redirect:/loginform";
        }
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/posts/read/" + postId;
        }

        commentService.add(postId, content.trim(), nickname);
        return "redirect:/posts/read/" + postId;
    }

    /** 대댓글 등록 */
    @PostMapping("/{parentId}/reply")
    public String reply(@PathVariable("postId") Long postId,
                        @PathVariable("parentId") Long parentId,
                        @RequestParam("content") String content,
                        @AuthenticationPrincipal(expression = "user.nickname") String nickname) {

        if (nickname == null || nickname.isBlank()) {
            return "redirect:/loginform";
        }
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/posts/read/" + postId;
        }

        commentService.reply(postId, parentId, content.trim(), nickname);
        return "redirect:/posts/read/" + postId;
    }

    /** 댓글 삭제 (본인/관리자) */
    @PostMapping("/{commentId}/delete")
    public String delete(@PathVariable("postId") Long postId,
                         @PathVariable("commentId") Long commentId,
                         @AuthenticationPrincipal(expression = "user.nickname") String nickname,
                         @AuthenticationPrincipal(expression = "user.role") String role) {

        boolean isAdmin = "ROLE_ADMIN".equals(role);
        commentService.delete(commentId, nickname, isAdmin);
        return "redirect:/posts/read/" + postId;
    }
}
