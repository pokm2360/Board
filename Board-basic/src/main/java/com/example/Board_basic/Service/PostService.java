package com.example.Board_basic.Service;

import com.example.Board_basic.Dto.PostDto;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<PostDto.Response> findAll() {
        return postRepository.findAll().stream()
                .map(PostDto.Response::new)
                .collect(Collectors.toList());
    }

    public Page<PostDto.Response> search(String keyword, Pageable pageable) {
        return postRepository.findByTitleContaining(keyword, pageable)
                .map(PostDto.Response::new);
    }

    public PostDto.Response findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + id));
        return new PostDto.Response(post);
    }

    @Transactional
    public Long save(PostDto.Request dto) {
        return postRepository.save(dto.toEntity()).getId();
    }

    @Transactional
    public void update(Long id, PostDto.Request dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + id));
        post.update(dto.getTitle(), dto.getContent());
    }

    @Transactional
    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new NoSuchElementException("Cannot delete non-existing post with id: " + id);
        }
        postRepository.deleteById(id);
    }
}


