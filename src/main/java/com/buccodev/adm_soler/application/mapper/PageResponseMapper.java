package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.core.pagination.PageResult;

import java.util.function.Function;

public final class PageResponseMapper {

    private PageResponseMapper() {
    }

    public static <T, D> PageResponse<D> toResponse(PageResult<T> result, Function<T, D> itemMapper) {
        return new PageResponse<>(
                result.content().stream().map(itemMapper).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
