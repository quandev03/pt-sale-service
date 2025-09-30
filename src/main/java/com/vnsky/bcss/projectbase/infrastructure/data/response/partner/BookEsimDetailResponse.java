package com.vnsky.bcss.projectbase.infrastructure.data.response.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEsimDetailResponse {
    private List<SaleOrderLineItem> saleOrderLines;
    private String note;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SaleOrderLineItem {
        private String id;
        private String pckCode;
        private Long quantity;
    }
}
