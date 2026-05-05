package com.example.ask_hub.auth.domain.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse (

    String accessToken,

    String refreshToken

){
}
