package com.example.ask_hub.auth.application;

import com.example.ask_hub.auth.domain.dto.request.JoinRequest;
import com.example.ask_hub.auth.domain.dto.request.LoginRequest;
import com.example.ask_hub.auth.domain.dto.response.TokenResponse;
import com.example.ask_hub.global.config.JwtProvider;
import com.example.ask_hub.global.exception.BusinessException;
import com.example.ask_hub.global.exception.ErrorCode;
import com.example.ask_hub.user.domain.entity.RefreshToken;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.infrastructure.RefreshTokenRepository;
import com.example.ask_hub.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    public void signup(JoinRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .company(request.company())
                .position(request.position())
                .joinedDate(request.joinedDate())
                .build();

        userRepository.save(user);
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.SIGN_OUT_USER);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        // 기존 RefreshToken 삭제 후 새로 저장
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰 재발급
    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        RefreshToken savedToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        refreshTokenRepository.delete(savedToken);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(newRefreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    // 로그아웃
    public void logout(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
    }

    // 회원복구
    public void restore(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.restore();
    }
}
