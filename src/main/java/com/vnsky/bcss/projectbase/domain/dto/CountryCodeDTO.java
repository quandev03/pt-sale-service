package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.database.annotation.DbColumnMapper;
import com.vnsky.excel.annotation.XlsxColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CountryCodeDTO {
    @XlsxColumn(readIndex = 0, writeIndex = 0, header = "CODE")
    @DbColumnMapper("COUNTRY_CODE")
    private String code;

    @DbColumnMapper("COUNTRY_NAME")
    @XlsxColumn(readIndex = 1, writeIndex = 1, header = "NAME")
    private String name;
}
