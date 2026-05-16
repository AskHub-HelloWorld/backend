package com.example.ask_hub.user.presentation;

import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.user.application.UserService;
import com.example.ask_hub.user.domain.dto.request.UserEmailRequest;
import com.example.ask_hub.user.domain.dto.request.UserModifyRequest;
import com.example.ask_hub.user.domain.dto.response.MyPageResponse;
import com.example.ask_hub.user.domain.dto.response.UserSearchResponse;
import com.example.ask_hub.user.domain.dto.response.UserSummaryResponse;
import com.example.ask_hub.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    @Operation(summary = "마이페이지 조회")
    public ResponseEntity<CommonResponse<MyPageResponse>> myPage(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok((userService.myPage(user.getId()))));
    }

    @PatchMapping("/signout")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<CommonResponse<Void>> signout(
            @AuthenticationPrincipal User user
    ) {
        userService.signout(user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(null));
    }

    @PostMapping("/email")
    @Operation(summary = "이메일 중복 확인")
    public ResponseEntity<CommonResponse<Void>> email(
            @RequestBody UserEmailRequest request
    ) {
        userService.email(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(null));
    }

    @GetMapping("/search")
    @Operation(summary = "회사명으로 사원 전체 검색")
    public ResponseEntity<CommonResponse<UserSearchResponse>> search(
            @RequestParam String company
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(userService.search(company)));
    }

    @PatchMapping
    @Operation(summary = "회원 정보 수정")
    public ResponseEntity<CommonResponse<Void>> modify(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserModifyRequest request
    ) {
        userService.modify(request, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok());
    }

    @GetMapping
    @Operation(summary = "내 활동 요약")
    public ResponseEntity<CommonResponse<UserSummaryResponse>> search(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(userService.summary(user.getId())));
    }
}