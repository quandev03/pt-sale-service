package com.vnsky.bcss.projectbase.shared.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IsoLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    public IsoLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText();
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        // Remove timezone indicator (Z or +HH:mm) and parse as LocalDateTime
        String cleaned = dateString.replaceAll("[Zz]|[+-]\\d{2}:?\\d{2}$", "");
        
        // Try parsing with various formats
        try {
            // Try ISO format without timezone
            if (cleaned.contains("T")) {
                if (cleaned.contains(".")) {
                    return LocalDateTime.parse(cleaned, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                } else {
                    return LocalDateTime.parse(cleaned, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                }
            } else {
                return LocalDateTime.parse(cleaned, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse LocalDateTime from: " + dateString, e);
        }
    }
}





