package com.example.Board_basic.Service;

import com.example.Board_basic.Dto.CommentDto;
import com.example.Board_basic.Entity.Comment;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Entity.User;
import com.example.Board_basic.Repository.CommentRepository;
import com.example.Board_basic.Repository.PostRepository;
import com.example.Board_basic.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(CommentDto.Request dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = Comment.builder()
                .comment(dto.getComment())
                .post(post)
                .user(user)
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void update(CommentDto.Request dto) {
        Comment comment = commentRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.updateContent(dto.getComment());
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}

