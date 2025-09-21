package com.vnsky.bcss.projectbase.shared.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ClassUtils {

    public static <C, T> Map<T, Field> getMapFieldByKey(ConditionGetKeyOfField<T> conditionKey, Class<C> clazz) {
        Map<T, Field> fieldMap = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            T key = conditionKey.getKey(field);
            if (Objects.equals(key, null)) continue;
            ReflectionUtils.makeAccessible(field);
            fieldMap.put(key, field);
        }
        return fieldMap;
    }

}
