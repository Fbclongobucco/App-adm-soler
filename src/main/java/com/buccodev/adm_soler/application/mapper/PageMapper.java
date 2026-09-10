package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.core.repository.Repository;

import java.util.function.Function;

public final class PageMapper {

    private PageMapper() {
    }

    public static <T, D> PageResponseDto<D> toResponseDto(Repository.PageResult<T> result,
                                                          Function<T, D> itemMapper) {
        return new PageResponseDto<>(
                result.content().stream().map(itemMapper).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages());
    }
}
