package com.example.Board_basic.Controller;

import com.example.Board_basic.Dto.PostDto;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Service.CommentService;
import com.example.Board_basic.Service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    // 목록 (페이징 + 검색)
    @GetMapping({"/", "/list"})
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "all") String scope,
                       Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Post> postPage = (keyword != null && !keyword.isBlank())
                ? postService.search(keyword, scope, pageable)
                : postService.findAll(pageable);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("scope", scope);
        return "list.html";
    }

    // 작성 폼
    @GetMapping("/write")
    public String writeForm() {
        return "write.html";
    }

    // 작성 처리
    @PostMapping("/writepost")
    public String write(@Valid @ModelAttribute PostDto dto,
                        BindingResult result,
                        @AuthenticationPrincipal(expression = "user.nickname") String nickname) {
        if (result.hasErrors()) return "write.html";
        if (nickname == null || nickname.isBlank()) return "redirect:/login";
        dto.setWriter(nickname);
        postService.save(dto);
        return "redirect:/";
    }

    // 상세
    @GetMapping("/posts/read/{id}")
    public String read(@PathVariable Long id,
                       HttpServletRequest request,
                       @AuthenticationPrincipal(expression = "username") String username,
                       Model model) {

        // 유니크 조회수
        String ip = request.getRemoteAddr();
        postService.increaseUniqueView(id, username, ip);

        PostDto post = postService.findById(id);
        model.addAttribute("posts", post);

        // 댓글 목록(평탄화)
        model.addAttribute("comments", commentService.listByPostFlat(id));

        // 좋아요 상태/개수
        long likeCount = postService.likeCount(id);
        boolean liked = (username != null) && postService.hasLiked(id, username);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("liked", liked);

        return "read.html";
    }

    // 좋아요 토글 (AJAX 요청 시 JSON 반환)
    @PostMapping("/posts/{id}/like")
    @ResponseBody
    public Map<String, Object> toggleLike(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String username = auth.getName();
        postService.toggleLike(id, username); // 토글 수행
        boolean likedNow = postService.hasLiked(id, username);
        long count = postService.likeCount(id);

        Map<String, Object> resp = new HashMap<>();
        resp.put("liked", likedNow);
        resp.put("count", count);
        return resp;
    }

    // 삭제
    @PostMapping("/posts/delete/{id}")
    public String delete(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/list";
    }

    // 검색 페이지
    @GetMapping("/posts/search")
    public String search(@RequestParam String keyword,
                         @RequestParam(defaultValue = "all") String scope,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Post> result = postService.search(keyword, scope, pageable);

        int last = Math.max(0, result.getTotalPages() - 1);
        if (page > last && result.getTotalPages() > 0) {
            pageable = PageRequest.of(last, 10, Sort.by("id").descending());
            result = postService.search(keyword, scope, pageable);
            page = last;
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("scope", scope);
        model.addAttribute("searchList", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        return "search.html";
    }
}
