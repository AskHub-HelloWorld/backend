package com.example.ask_hub.user.presentation;

import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.user.application.UserService;
import com.example.ask_hub.user.domain.dto.response.MyPageResponse;
import com.example.ask_hub.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
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
}