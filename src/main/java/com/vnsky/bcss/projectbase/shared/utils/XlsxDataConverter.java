package com.vnsky.bcss.projectbase.shared.utils;

@FunctionalInterface
public interface XlsxDataConverter {
    <T> String convert(T t);
}
