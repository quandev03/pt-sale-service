package com.vnsky.bcss.projectbase.shared.utils;

@FunctionalInterface
public interface ConditionShowValue {

    <T> boolean willShowValue(Class<T> clazz, T data);
}
