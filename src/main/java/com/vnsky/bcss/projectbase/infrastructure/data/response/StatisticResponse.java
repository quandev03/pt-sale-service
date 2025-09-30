package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticResponse {
    @DbColumnMapper("STAT_DATE")
    private LocalDate date;

    @DbColumnMapper("STAT_YEAR")
    private int year;

    @DbColumnMapper("STAT_WEEK")
    private int week;

    @DbColumnMapper("STAT_LABEL")
    private String label;

    @DbColumnMapper("STAT_COUNT")
    private Long count;
}
