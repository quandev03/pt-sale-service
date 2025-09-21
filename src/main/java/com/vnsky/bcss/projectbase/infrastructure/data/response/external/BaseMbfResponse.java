package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.Data;

@Data
public class BaseMbfResponse<T> {
    private String code;
    private String description;
    private T data;
} 