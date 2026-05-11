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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
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

    @PostMapping("/messages")
    @Operation(summary = "메세지 보내기")
    public ResponseEntity<CommonResponse<MessageCreateResponse>> sendMessage(
            @RequestBody MessageCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(sessionService.sendMessage(request, user.getId())));
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


}
