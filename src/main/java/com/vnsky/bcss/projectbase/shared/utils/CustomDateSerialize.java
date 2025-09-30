package com.vnsky.bcss.projectbase.shared.utils;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.format.DateTimeFormatter;

@SuppressWarnings("all")
public class CustomDateSerialize extends LocalDateTimeSerializer {
    public CustomDateSerialize(DateTimeFormatter f) {
        super(f);
    }

    public CustomDateSerialize() {
        this(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
