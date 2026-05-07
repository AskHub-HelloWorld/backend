package com.example.ask_hub.global.domain;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final boolean hasNext;

    private SliceResponse(Slice<T> slice) {
        this.content = slice.getContent();
        this.page = slice.getNumber();
        this.size = slice.getSize();
        this.hasNext = slice.hasNext();
    }

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(slice);
    }
}