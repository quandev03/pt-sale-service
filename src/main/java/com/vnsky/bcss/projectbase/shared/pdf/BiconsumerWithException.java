package com.vnsky.bcss.projectbase.shared.pdf;

@FunctionalInterface

public interface BiconsumerWithException<T, U> {

    void accept(T t, U u);
}
