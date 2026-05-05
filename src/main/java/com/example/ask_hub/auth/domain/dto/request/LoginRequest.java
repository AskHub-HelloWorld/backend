package com.example.ask_hub.auth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(

        @NotBlank(message = "공백이 포함되어있습니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "공백이 포함되어있습니다.")
        String password

) {
}
