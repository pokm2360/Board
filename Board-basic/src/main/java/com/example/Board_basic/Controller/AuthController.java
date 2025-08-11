package com.example.Board_basic.Controller;

import com.example.Board_basic.Entity.User;
import com.example.Board_basic.Repository.UserRepository;
import com.example.Board_basic.Service.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 페이지
    @GetMapping("/loginform")
    public String login() {
        return "login.html"; // templates/login.html
    }

    // 회원가입 폼
    @GetMapping("/join")
    public String joinForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "join.html";
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("user") User user,
                       BindingResult result,
                       Model model) {

        boolean hasError = false;

        // 널가드 (바인딩 실패 등)
        if (user == null) {
            result.reject("bind.error", "요청 바인딩에 실패했습니다. 다시 시도해주세요.");
            return "join.html";
        }

        // 아이디 중복 체크
        if (user.getUsername() != null &&
                userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("valid_username", "이미 존재하는 아이디입니다.");
            hasError = true;
        }

        // 이메일 중복 체크
        if (user.getEmail() != null &&
                userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("valid_email", "이미 가입된 이메일입니다.");
            hasError = true;
        }

        if (hasError || result.hasErrors()) {
            return "join.html"; // 에러 시 가입 페이지로 복귀
        }

        // 비밀번호 암호화 및 기본 권한 부여
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "login.html"; // 가입 후 로그인 페이지로 이동
    }
}
