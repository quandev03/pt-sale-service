package com.vnsky.bcss.projectbase.shared.utils;

import java.lang.reflect.Field;

@FunctionalInterface
public interface ConditionGetKeyOfField<T> {
    T getKey(Field field);

}
