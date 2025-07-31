package com.example.Board_basic.Controller;

import com.example.Board_basic.Service.PrincipalDetails;
import com.example.Board_basic.Dto.PostDto;
import com.example.Board_basic.Service.CommentService;
import com.example.Board_basic.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "posts/list";
    }

    @GetMapping("/write")
    public String writeForm(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        model.addAttribute("user", principal.getUser());
        return "posts/write";
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> write(@RequestBody PostDto.Request dto) {
        postService.save(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/read/{id}")
    public String read(@PathVariable Long id, Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        PostDto.Response post = postService.findById(id);
        model.addAttribute("posts", post);
        model.addAttribute("user", principal != null ? principal.getUser() : null);
        model.addAttribute("writer", principal != null && post.getUserId().equals(principal.getUser().getId()));
        model.addAttribute("comments", post.getComments());
        return "posts/read";
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        model.addAttribute("posts", postService.findById(id));
        return "posts/update";
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PostDto.Request dto) {
        postService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Pageable pageable, Model model) {
        Page<PostDto.Response> results = postService.search(keyword, pageable);
        model.addAttribute("searchList", results.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("hasPrev", results.hasPrevious());
        model.addAttribute("hasNext", results.hasNext());
        model.addAttribute("previous", results.previousPageable().getPageNumber());
        model.addAttribute("next", results.nextPageable().getPageNumber());
        return "posts/search";
    }
}
