package com.vnsky.bcss.projectbase.shared.utils;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.format.DateTimeFormatter;

@SuppressWarnings("all")
public class CustomDateDeserializer extends LocalDateTimeDeserializer {
    public CustomDateDeserializer() {
        this(DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm:ss"));
    }

    public CustomDateDeserializer(DateTimeFormatter formatter) {
        super(formatter);
    }
}
