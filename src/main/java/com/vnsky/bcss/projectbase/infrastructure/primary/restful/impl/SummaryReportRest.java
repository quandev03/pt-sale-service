package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.SummaryAllReportDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryByOrgReportResponseDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.SummaryReportServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.SummaryReportOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller implementation for Summary Report
 */
@RestController
@RequiredArgsConstructor
public class SummaryReportRest implements SummaryReportOperation {

    private final SummaryReportServicePort summaryReportServicePort;

    @Override
    public ResponseEntity<Object> searchSummaryByOrgReport(String startDate, String endDate, Pageable pageable) {
        SummaryByOrgReportResponseDTO result = summaryReportServicePort.searchSummaryByOrgReport(startDate, endDate, pageable);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Object> searchSummaryAllReport(String startDate, String endDate) {
        SummaryAllReportDTO result = summaryReportServicePort.searchSummaryAllReport(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Resource> exportSummaryByOrgReport(String startDate, String endDate) {
        Resource resource = summaryReportServicePort.exportSummaryByOrgReport(startDate, endDate);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> exportSummaryAllReport(String startDate, String endDate) {
        Resource resource = summaryReportServicePort.exportSummaryAllReport(startDate, endDate);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
