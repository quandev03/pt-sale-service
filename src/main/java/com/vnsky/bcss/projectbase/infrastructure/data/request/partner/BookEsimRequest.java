package com.vnsky.bcss.projectbase.infrastructure.data.request.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vnsky.bcss.projectbase.infrastructure.data.deserializer.QuantityDeserializer;
import com.vnsky.bcss.projectbase.infrastructure.data.validation.ValidQuantity;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEsimRequest {
    @NotNull(message = Constant.ErrorMessage.FIELD_REQUIRED)
    @ValidQuantity
    @JsonDeserialize(using = QuantityDeserializer.class)
    private Integer quantity;

    @NotNull(message = Constant.ErrorMessage.FIELD_REQUIRED)
    @NotBlank(message = Constant.ErrorMessage.FIELD_REQUIRED)
    private String packageCode;
}
