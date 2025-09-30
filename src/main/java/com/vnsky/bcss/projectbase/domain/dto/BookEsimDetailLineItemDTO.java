package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEsimDetailLineItemDTO {
    @DbColumnMapper("ID")
    private String id;
    
    @DbColumnMapper("PCK_CODE")
    private String pckCode;
    
    @DbColumnMapper("QUANTITY")
    private Long quantity;
}
