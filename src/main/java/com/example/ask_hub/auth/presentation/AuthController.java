package com.example.ask_hub.auth.presentation;

import com.example.ask_hub.auth.application.AuthService;
import com.example.ask_hub.auth.domain.dto.request.JoinRequest;
import com.example.ask_hub.auth.domain.dto.request.LoginRequest;
import com.example.ask_hub.auth.domain.dto.response.TokenResponse;
import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.user.domain.dto.request.UserEmailRequest;
import com.example.ask_hub.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<CommonResponse<Void>> signup(
            @RequestBody
            @Valid
            JoinRequest request
    ) {
        authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok());
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<CommonResponse<TokenResponse>> login(
            @RequestBody
            @Valid
            LoginRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(authService.login(request)));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급")
    public ResponseEntity<CommonResponse<TokenResponse>> reissue(
            @RequestHeader("Authorization")
            String refreshToken
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(authService.reissue(refreshToken)));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<CommonResponse<Void>> logout(
            @AuthenticationPrincipal User user
    ) {
        authService.logout(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok());
    }

    @PatchMapping("/restore")
    @Operation(summary = "회원 복구")
    public ResponseEntity<CommonResponse<Void>> restore(
            @RequestBody
            @Valid
            LoginRequest request
    ) {
        authService.restore(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok());
    }

    @PostMapping("/email")
    @Operation(summary = "이메일 중복 확인")
    public ResponseEntity<CommonResponse<Void>> email(
            @RequestBody @Valid UserEmailRequest request
    ) {
        authService.email(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(null));
    }
}
