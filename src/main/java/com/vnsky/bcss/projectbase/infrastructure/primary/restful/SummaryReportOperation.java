package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller interface for Summary Report
 */
@Tag(name = "Summary Report", description = "API báo cáo tổng hợp")
@RequestMapping({"${application.path.base.private}/reports"})
public interface SummaryReportOperation {

    @Operation(summary = "Tìm kiếm báo cáo kết quả thuê bao - gói cước")
    @GetMapping(value = "/summary-by-org", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> searchSummaryByOrgReport(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", example = "2024-01-31")
            @RequestParam(required = false) String endDate,
            @PageableDefault Pageable pageable);

    @Operation(summary = "Tìm kiếm báo cáo tổng hợp kết quả")
    @GetMapping(value = "/summary-all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> searchSummaryAllReport(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", example = "2024-01-31")
            @RequestParam(required = false) String endDate);

    @Operation(summary = "Xuất báo cáo tổng hợp kết quả thuê bao - gói cước")
    @PostMapping(value = "/summary-by-org/export")
    ResponseEntity<Resource> exportSummaryByOrgReport(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", example = "2024-01-31")
            @RequestParam(required = false) String endDate);

    @Operation(summary = "Xuất báo cáo tổng hợp kết quả")
    @PostMapping(value = "/summary-all/export")
    ResponseEntity<Resource> exportSummaryAllReport(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", example = "2024-01-31")
            @RequestParam(required = false) String endDate);
}
