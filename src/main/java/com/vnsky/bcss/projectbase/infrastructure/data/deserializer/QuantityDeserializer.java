package com.vnsky.bcss.projectbase.infrastructure.data.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;

import java.io.IOException;

public class QuantityDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer getNullValue(DeserializationContext ctxt) {
        throw BaseException.badRequest(ErrorCode.INVALID_QUANTITY_NULL).build();
    }

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Handle null values
        if (node == null || node.isNull()) {
            throw BaseException.badRequest(ErrorCode.INVALID_QUANTITY_NULL).build();
        }

        // Handle non-numeric values
        if (!node.isNumber() && !node.isTextual()) {
            throw BaseException.badRequest(ErrorCode.INVALID_QUANTITY_FORMAT).build();
        }

        try {
            // Try to parse as integer
            if (node.isTextual()) {
                String textValue = node.asText();
                int value = Integer.parseInt(textValue);
                if (value <= 0) {
                    throw BaseException.badRequest(ErrorCode.INVALID_QUANTITY_FORMAT).build();
                }
                return value;
            } else {
                int value = node.asInt();
                if (value <= 0) {
                    throw BaseException.badRequest(ErrorCode.INVALID_QUANTITY_FORMAT).build();
                }
                return value;
            }
        } catch (NumberFormatException e) {
            throw BaseException.badRequest(ErrorCode.INVALID_QUANTITY_FORMAT).build();
        }
    }
}
