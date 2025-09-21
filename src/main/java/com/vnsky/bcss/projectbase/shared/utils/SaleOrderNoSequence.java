package com.vnsky.bcss.projectbase.shared.utils;

import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ValueGenerationType(generatedBy = SaleOrderNoSequenceGenerator.class)
@Retention(RUNTIME)
@Target({METHOD,FIELD})
public @interface SaleOrderNoSequence {
}
