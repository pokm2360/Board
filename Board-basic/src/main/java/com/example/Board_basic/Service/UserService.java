package com.example.Board_basic.Service;

import com.example.Board_basic.Dto.UserDto;
import com.example.Board_basic.Entity.User;
import com.example.Board_basic.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public void update(UserDto.Request dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("수정할 사용자가 존재하지 않습니다. id=" + id));

        User updated = user.toBuilder()
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                .build();

        userRepository.save(updated);
    }

    @Transactional
    public void join(UserDto.Request dto) {
        userRepository.save(dto.toEntity());
    }
}

