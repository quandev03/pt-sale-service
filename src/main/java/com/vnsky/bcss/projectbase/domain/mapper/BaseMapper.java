package com.vnsky.bcss.projectbase.domain.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BaseMapper<E, D> {
    E toEntity(D dto);

    D toDto(E entity);

    List<E> toListEntity(List<D> dtoList);

    List<D> toListDto(List<E> entities);

    default Page<D> toPageDto(List<E> entities) {
        List<D> dtos = toListDto(entities);
        int size = dtos.size();
        return new PageImpl<>(dtos, PageRequest.of(0, size > 0 ? size : 1), size);
    }
}

