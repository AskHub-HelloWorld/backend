package com.example.ask_hub.user.domain.dto.request;

import com.example.ask_hub.user.domain.enums.Position;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserModifyRequest(

        @NotEmpty
        String name,

        @NotEmpty
        String company,

        @NotNull
        Position position,

        @Email
        String email,

        @NotNull
        LocalDate joinedDate
) {
}
