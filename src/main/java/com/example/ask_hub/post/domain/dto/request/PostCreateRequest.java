package com.example.ask_hub.post.domain.dto.request;

import com.example.ask_hub.user.domain.enums.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record PostCreateRequest(

        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotNull
        Boolean isAnonymous,

        @NotNull(message = "내용은 공백일 수 없습니다.")
        String content,

        @NotNull(message = "게시글 채택 시 포인트를 명시해야합니다.")
        Integer point,

        @NotEmpty(message = "개발 직군을 명시해야합니다.")
        Position position

) {
}
