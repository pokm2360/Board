package com.example.Board_basic.Config;

import com.example.Board_basic.Entity.User;
import com.example.Board_basic.Service.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalUserInfoAdvice {

    @ModelAttribute("loginUser") // <- 이름을 loginUser로 변경
    public User loginUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return principalDetails != null ? principalDetails.getUser() : null;
    }
}
