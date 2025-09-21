package com.vnsky.bcss.projectbase.shared.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XlsxColumn {
    int index();

    int ignoreIndex() default -1;

    String header();

    String headerCsv() default "";

    boolean ignore() default false;

    String converter() default "";

    boolean isImageColumn() default false;
}
