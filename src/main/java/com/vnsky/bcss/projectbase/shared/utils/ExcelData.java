package com.vnsky.bcss.projectbase.shared.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ExcelData<T> {
    private Map<Integer, String> headerLineNames;
    private List<T> dataLines;
    private boolean includeRowNum;

    public ExcelData(Map<Integer, String> headerLineNames, List<T> dataLines, boolean includeRowNum) {
        this.headerLineNames = headerLineNames;
        this.dataLines = dataLines;
        this.includeRowNum = includeRowNum;
    }

    public ExcelData(Map<Integer, String> headerLineNames, List<T> dataLines) {
        this(headerLineNames, dataLines, true);
    }

    public String getHeaderLineNamesOf(Integer k, String defaultValue) {
        return headerLineNames.getOrDefault(k, defaultValue);
    }
}