package com.vnsky.bcss.projectbase.shared.utils;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;

@FunctionalInterface
public interface ErrorTranslator {

    String apply(ErrorCode errorKey, Object... args);

}
