package com.example.Board_basic.Dto;

import com.example.Board_basic.Entity.User;
import lombok.*;

public class UserDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String username;
        private String password;
        private String nickname;
        private String email;
        private User.UserRole role;

        public User toEntity() {
            return User.builder()
                    .username(username)
                    .password(password)
                    .nickname(nickname)
                    .email(email)
                    .role(role != null ? role : User.UserRole.USER)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private User.UserRole role;
    }
}
