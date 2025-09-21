package com.vnsky.bcss.projectbase.infrastructure.data.request.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEsimRequest {
    private Integer quantity;
    private String packageCode;
}
