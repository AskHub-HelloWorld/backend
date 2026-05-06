package com.example.ask_hub.auth.domain.dto.request;

import com.example.ask_hub.user.domain.enums.Position;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record JoinRequest(

        @NotBlank(message = "공백이 포함되어있습니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내여야 합니다.")
        String password,

        @NotBlank(message = "공백이 포함되어있습니다.")
        String name,

        @NotBlank(message = "회사명은 필수입니다.")
        String company,

        @NotNull(message = "개발직군은 필수입니다")
        Position position,

        @NotNull(message = "입사일자는 필수입니다.")
        LocalDate joinedDate

) {
}
