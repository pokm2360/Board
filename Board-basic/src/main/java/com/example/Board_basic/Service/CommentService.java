package com.example.Board_basic.Service;

import com.example.Board_basic.Dto.CommentDto;
import com.example.Board_basic.Entity.Comment;
import com.example.Board_basic.Entity.Post;
import com.example.Board_basic.Repository.CommentRepository;
import com.example.Board_basic.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 댓글을 "부모 -> 자식" 순서로 정렬해 들여쓰기(depth)와 함께 평탄화해서 반환
     */
    public List<CommentDto> listByPostFlat(Long postId) {
        // 모든 댓글을 한 번에 가져와서 메모리에서 트리 순서로 정렬
        List<Comment> all = commentRepository.findByPostIdOrderByCreatedDateAsc(postId);

        // parentId -> children 목록 맵 구성
        Map<Long, List<Comment>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getParent() == null ? 0L : c.getParent().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 루트 댓글(부모 null)
        List<Comment> roots = childrenMap.getOrDefault(0L, Collections.emptyList());
        roots.sort(Comparator.comparing(Comment::getCreatedDate).thenComparing(Comment::getId));

        List<CommentDto> result = new ArrayList<>();
        for (Comment root : roots) {
            dfsFlatten(root, 0, childrenMap, result);
        }
        return result;
    }

    private void dfsFlatten(Comment node, int depth,
                            Map<Long, List<Comment>> childrenMap,
                            List<CommentDto> out) {
        CommentDto dto = CommentDto.fromEntity(node);
        dto.setDepth(depth); // 보여줄 들여쓰기는 여기서 강제 설정
        out.add(dto);

        List<Comment> children = childrenMap.getOrDefault(node.getId(), Collections.emptyList());
        children.sort(Comparator.comparing(Comment::getCreatedDate).thenComparing(Comment::getId));
        for (Comment ch : children) {
            dfsFlatten(ch, depth + 1, childrenMap, out);
        }
    }

    // 원댓글
    public Long add(Long postId, String content, String nickname) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Comment c = commentRepository.save(Comment.builder()
                .post(post)
                .parent(null)
                .depth(0)
                .content(content)
                .writer(nickname)
                .createdDate(LocalDateTime.now())
                .build());
        return c.getId();
    }

    // 대댓글
    public Long reply(Long postId, Long parentId, String content, String nickname) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

        // 안전 가드: 부모 댓글이 같은 글의 댓글인지 확인
        if (!Objects.equals(parent.getPost().getId(), postId)) {
            throw new IllegalArgumentException("부모 댓글이 해당 게시글의 댓글이 아닙니다.");
        }

        Comment c = commentRepository.save(Comment.builder()
                .post(post)
                .parent(parent)
                .depth(parent.getDepth() + 1)
                .content(content)
                .writer(nickname)
                .createdDate(LocalDateTime.now())
                .build());
        return c.getId();
    }

    public void delete(Long commentId, String nickname, boolean isAdmin) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        if (!isAdmin && !c.getWriter().equals(nickname)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(c);
    }
}
