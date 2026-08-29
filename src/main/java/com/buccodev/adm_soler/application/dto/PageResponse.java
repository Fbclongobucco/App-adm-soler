package com.buccodev.adm_soler.application.dto;

import com.buccodev.adm_soler.core.repository.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public static <D, T> PageResponse<D> from(PageResult<T> result, Function<T, D> mapper) {
        return new PageResponse<>(
                result.content().stream().map(mapper).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
