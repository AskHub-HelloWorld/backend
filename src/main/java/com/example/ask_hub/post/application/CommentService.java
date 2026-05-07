package com.example.ask_hub.post.application;

import com.example.ask_hub.global.domain.SliceResponse;
import com.example.ask_hub.global.exception.BusinessException;
import com.example.ask_hub.global.exception.ErrorCode;
import com.example.ask_hub.post.domain.dto.request.CommentCreateRequest;
import com.example.ask_hub.post.domain.dto.response.CommentGetResponse;
import com.example.ask_hub.post.domain.entity.Comment;
import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.post.infrastructure.CommentRepository;
import com.example.ask_hub.post.infrastructure.PostRepository;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public SliceResponse<CommentGetResponse> getList(Long postId, Long userId, Pageable pageable) {

        Slice<Comment> slice = commentRepository.findAllByPostId(postId, pageable);

        Slice<CommentGetResponse> responses = slice.map(
                comment -> CommentGetResponse.from(comment, userId)
        );

        return SliceResponse.from(responses);
    }


    public Long create(CommentCreateRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(request.content())
                .parent(null)
                .isAnonymous(request.isAnonymous())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);
        return comment.getId();
    }
}
