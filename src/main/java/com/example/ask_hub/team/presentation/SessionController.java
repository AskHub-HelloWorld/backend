package com.example.ask_hub.team.presentation;

import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.global.domain.SliceResponse;
import com.example.ask_hub.team.application.SessionService;
import com.example.ask_hub.team.domain.dto.request.MessageCreateRequest;
import com.example.ask_hub.team.domain.dto.response.MessageCreateResponse;
import com.example.ask_hub.team.domain.dto.response.MessageGetResponse;
import com.example.ask_hub.team.domain.dto.response.SessionGetResponse;
import com.example.ask_hub.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "내 세션 목록 조회")
    public ResponseEntity<CommonResponse<SliceResponse<SessionGetResponse>>> get(
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(sessionService.get(user.getId(), pageable)));
    }

    @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,  produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "메세지 보내기")
    public ResponseEntity<CommonResponse<MessageCreateResponse>> sendMessage(
            @RequestParam (required = false) List<MultipartFile> files,

            @RequestParam @NotNull Long teamId,

            @RequestParam @NotNull Long sessionId,

            @RequestParam @NotNull String message,

            @AuthenticationPrincipal User user
    ) {
        MessageCreateRequest request = new MessageCreateRequest(sessionId, teamId, message);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(sessionService.sendMessage(files, request, user.getId())));
    }

    @GetMapping("/{sessionId}/messages")
    @Operation(summary = "메세지 목록 조회")
    public ResponseEntity<CommonResponse<SliceResponse<MessageGetResponse>>> getMessages(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(sessionService.getMessages(sessionId, user.getId())));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "세션 삭제")
    public ResponseEntity<CommonResponse<Void>> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long sessionId
    ) {
        sessionService.delete(sessionId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok());
    }

    @GetMapping("/search")
    @Operation(summary = "세션 검색")
    public ResponseEntity<CommonResponse<SliceResponse<SessionGetResponse>>> search(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(sessionService.search(keyword, user.getId(), pageable)));
    }
}
