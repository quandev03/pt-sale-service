package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberPackageDTO {
    @DbColumnMapper("ID")
    private String id;
    @DbColumnMapper("PCK_PRICE")
    private Long packagePrice;
}
