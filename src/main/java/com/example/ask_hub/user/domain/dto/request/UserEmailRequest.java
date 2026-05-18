package com.example.ask_hub.user.domain.dto.request;

import jakarta.validation.constraints.Email;

public record UserEmailRequest(
        @Email
        String email
) {
}
