package com.example.ask_hub.post.presentation;

import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.global.domain.SliceResponse;
import com.example.ask_hub.post.application.PostService;
import com.example.ask_hub.post.domain.dto.request.PostCreateRequest;
import com.example.ask_hub.post.domain.dto.response.PostDetailResponse;
import com.example.ask_hub.post.domain.dto.response.PostGetResponse;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.domain.enums.Position;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시글 도메인")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시글 생성")
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
    @Operation(summary = "전체 게시글 목록 조회(null 값은 익명을 뜻합니다)")
    public ResponseEntity<CommonResponse<SliceResponse<PostGetResponse>>> getList(
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.getList(user.getId(), pageable)));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회")
    public ResponseEntity<CommonResponse<PostDetailResponse>> get(
            @PathVariable Long postId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.getDetail(postId)));
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "게시글 답변 채택")
    public ResponseEntity<CommonResponse<Void>> select(
            @PathVariable Long postId,
            @RequestParam Long commentId,
            @AuthenticationPrincipal User user
    ) {
        postService.select(postId, commentId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok());
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 검색 목록 조회")
    public ResponseEntity<CommonResponse<SliceResponse<PostGetResponse>>> search(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.search(keyword, user.getId(), pageable)));
    }

    @GetMapping("/category")
    @Operation(summary = "개발 직군 별 게시글 목록 조회(단일 카테고리 별 조회)")
    public ResponseEntity<CommonResponse<SliceResponse<PostGetResponse>>> category(
            @RequestParam Position category, // 일단은 단일로
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.category(category, user.getId(), pageable)));
    }

    @GetMapping("/my")
    @Operation(summary = "내가 작성한 게시글 목록 조회")
    public ResponseEntity<CommonResponse<SliceResponse<PostGetResponse>>> my(
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.my(user.getId(), pageable)));
    }

    @GetMapping("/unresolved")
    @Operation(summary = "미채택 게시글 목록 조회")
    public ResponseEntity<CommonResponse<SliceResponse<PostGetResponse>>> unresolved(
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(postService.unresolved(user.getId(), pageable)));
    }
}
