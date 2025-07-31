package com.example.Board_basic.Controller;

import com.example.Board_basic.Dto.UserDto;
import com.example.Board_basic.Service.PrincipalDetails;
import com.example.Board_basic.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/auth/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/auth/joinProc")
    public String join(@ModelAttribute UserDto.Request dto) {
        userService.join(dto);
        return "redirect:/auth/login";
    }

    @GetMapping("/modify")
    public String modifyForm(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        model.addAttribute("user", principal.getUser());
        return "user/modify";
    }

    @PutMapping("/api/user")
    @ResponseBody
    public ResponseEntity<?> modify(@RequestBody UserDto.Request dto) {
        userService.update(dto);
        return ResponseEntity.ok().build();
    }
}
