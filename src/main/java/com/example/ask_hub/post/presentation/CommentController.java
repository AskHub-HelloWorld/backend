package com.example.ask_hub.post.presentation;

import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.global.domain.SliceResponse;
import com.example.ask_hub.post.application.CommentService;
import com.example.ask_hub.post.domain.dto.request.CommentCreateRequest;
import com.example.ask_hub.post.domain.dto.response.CommentGetResponse;
import com.example.ask_hub.user.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommonResponse<SliceResponse<CommentGetResponse>>> getList(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(commentService.getList(postId, user.getId(), pageable)));
    }

    @PostMapping("/api/posts/comments")
    public ResponseEntity<CommonResponse<Long>> create(
            @RequestBody @Valid CommentCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok(commentService.create(request, user.getId())));
    }

}
