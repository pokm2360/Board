package com.example.Board_basic.Controller;

import com.example.Board_basic.Dto.PostDto;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Service.CommentService;
import com.example.Board_basic.Service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;


    // 게시글 목록 (페이징 + 검색)
    @GetMapping({"/", "/list"})
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Post> postPage;

        if (keyword != null && !keyword.isBlank()) {
            postPage = postService.search(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            postPage = postService.findAll(pageable);
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "list.html";
    }

    // 게시글 작성 폼
    @GetMapping("/write")
    public String writeForm() {
        return "write.html";
    }

    // 게시글 작성 처리
    @PostMapping("/writepost")
    public String write(@Valid @ModelAttribute PostDto dto,
                        BindingResult result,
                        @AuthenticationPrincipal(expression = "user.nickname") String nickname) {
        if (result.hasErrors()) {
            return "write.html";
        }
        if (nickname == null || nickname.isBlank()) { // 비로그인/이상치 가드
            return "redirect:/login";
        }

        dto.setWriter(nickname); // ★ 작성자 = 닉네임
        postService.save(dto);
        return "redirect:/";
    }

    // 게시글 상세 조회
    @GetMapping("/posts/read/{id}")
    public String read(@PathVariable Long id, Model model) {
        PostDto post = postService.findById(id);
        model.addAttribute("posts", post);
        // 댓글 목록 추가
        model.addAttribute("comments", commentService.listByPost(id));
        return "read.html";
    }

    // 게시글 삭제
    @PostMapping("/posts/delete/{id}")
    public String delete(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/list";
    }

}



