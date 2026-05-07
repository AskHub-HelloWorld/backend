package com.example.ask_hub.post.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(

        @NotBlank(message = "내용은 공백일 수 없습니다.")
        String content,

        @NotNull(message = "익명 여부를 선택해주십시오.")
        Boolean isAnonymous,

        @NotNull(message = "게시글 정보를 입력해주십시오.")
        Long postId

) {
}
