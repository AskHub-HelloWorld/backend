package com.example.ask_hub.post.application;

import com.example.ask_hub.global.domain.SliceResponse;
import com.example.ask_hub.global.exception.BusinessException;
import com.example.ask_hub.global.exception.ErrorCode;
import com.example.ask_hub.post.domain.dto.request.PostCreateRequest;
import com.example.ask_hub.post.domain.dto.response.PostCategoryResponse;
import com.example.ask_hub.post.domain.dto.response.PostDetailResponse;
import com.example.ask_hub.post.domain.dto.response.PostGetResponse;
import com.example.ask_hub.post.domain.dto.response.PostSearchResponse;
import com.example.ask_hub.post.domain.entity.Comment;
import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.post.infrastructure.CommentRepository;
import com.example.ask_hub.post.infrastructure.PostRepository;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.domain.enums.Position;
import com.example.ask_hub.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    public Long create(PostCreateRequest request, Long userId) {

        Optional<Post> existingPost = postRepository.findByTitle(request.title());

        if (existingPost.isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_TITLE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .user(user)
                .isAnonymous(request.isAnonymous())
                .point(request.point())
                .position(request.position())
                .build();

        postRepository.save(post);

        return post.getId();
    }

    public SliceResponse<PostGetResponse> getList(Long userId, Pageable pageable) {

        Slice<Post> slice = postRepository.findAll(pageable); // order by는 pageable에

        Slice<PostGetResponse> responses = slice.map(
                post -> PostGetResponse.from(post, post.getUser() ,userId, commentRepository.countByPostId(post.getId()))
        );

        return SliceResponse.from(responses);
    }

    public PostDetailResponse getDetail(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        return PostDetailResponse.from(post, commentRepository.countByPostId(post.getId()));
    }

    public Long select(Long postId, Long commentId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NOT_ALLOWED_USER);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        post.select(comment);
        return post.getId();
    }

    public SliceResponse<PostGetResponse> search(String keyword, Long userId, Pageable pageable) {

        Slice<Post> slice = postRepository.findByTitleContaining(keyword, pageable);

        Slice<PostGetResponse> responses = slice.map(
                post -> PostGetResponse.from(post, post.getUser(), userId, commentRepository.countByPostId(post.getId()))
        );

        return SliceResponse.from(responses);
    }

    public SliceResponse<PostGetResponse> category(Position category, Long userId, Pageable pageable) {

        Slice<Post> slice = postRepository.findAllByPosition(category, pageable);

        Slice<PostGetResponse> responses = slice.map(
                post -> PostGetResponse.from(post, post.getUser(), userId, commentRepository.countByPostId(post.getId()))
        );

        return SliceResponse.from(responses);
    }


    public SliceResponse<PostGetResponse> my(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Slice<Post> slice = postRepository.findAllByUserId(user.getId(), pageable);

        Slice<PostGetResponse> responses = slice.map(
                post -> PostGetResponse.from(post, post.getUser(), userId, commentRepository.countByPostId(post.getId()))
        );

        return SliceResponse.from(responses);
    }

    public SliceResponse<PostGetResponse> unresolved(Long userId, Pageable pageable) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Slice<Post> slice = postRepository.findAllByCommentIsNull(pageable);

        Slice<PostGetResponse> responses = slice.map(
                post -> PostGetResponse.from(post, post.getUser(), userId, commentRepository.countByPostId(post.getId()))
        );

        return SliceResponse.from(responses);
    }
}
