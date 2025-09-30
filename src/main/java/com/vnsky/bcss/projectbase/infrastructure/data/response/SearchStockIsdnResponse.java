package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchStockIsdnResponse {
    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("ISDN")
    private Long isdn;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    @DbColumnMapper("UI_STATUS")
    private Integer status;
}
