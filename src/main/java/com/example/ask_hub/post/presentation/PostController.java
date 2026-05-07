package com.example.ask_hub.post.presentation;

import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.global.domain.SliceResponse;
import com.example.ask_hub.post.application.PostService;
import com.example.ask_hub.post.domain.dto.request.PostCreateRequest;
import com.example.ask_hub.post.domain.dto.response.PostCategoryResponse;
import com.example.ask_hub.post.domain.dto.response.PostDetailResponse;
import com.example.ask_hub.post.domain.dto.response.PostGetResponse;
import com.example.ask_hub.post.domain.dto.response.PostSearchResponse;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.domain.enums.Position;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CommonResponse<Long>> create(
            @RequestBody
            @Valid
            PostCreateRequest request,

            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok(postService.create(request, user.getId())));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<SliceResponse<PostGetResponse>>> getList(
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.getList(user.getId(), pageable)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<CommonResponse<PostDetailResponse>> get(
            @PathVariable Long postId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.getDetail(postId)));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<CommonResponse<Long>> select(
            @PathVariable Long postId,
            @RequestParam Long commentId,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.select(postId, commentId, user.getId())));
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<SliceResponse<PostSearchResponse>>> search(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.search(keyword, user.getId(), pageable)));
    }

    @GetMapping("/category")
    public ResponseEntity<CommonResponse<SliceResponse<PostCategoryResponse>>> category(
            @RequestParam Position category, // 일단은 단일로
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.category(category, user.getId(), pageable)));
    }

}
