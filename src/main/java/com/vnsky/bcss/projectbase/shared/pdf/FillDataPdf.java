package com.vnsky.bcss.projectbase.shared.pdf;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FillDataPdf {

    String keyFill() default "";

    int fontSize() default 12;

    String fontName() default "Arial";

    boolean image() default false;

    int maxWidth() default 800;


}
