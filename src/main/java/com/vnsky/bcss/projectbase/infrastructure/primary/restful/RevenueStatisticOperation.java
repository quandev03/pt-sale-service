package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.common.annotation.RequestParamsObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Revenue Report Operation", description = "API liên quan đến báo cáo")
@RequestMapping("${application.path.base.public}/revenue-statistic")
public interface RevenueStatisticOperation {

    @GetMapping("/order")
    @Operation(summary = "Search order revenue report")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> searchOrderRevenueReport(@RequestParamsObject SearchRevenueReportRequest request,
                                                    @PageableDefault Pageable pageable);

    @PostMapping("/order/export-excel")
    @Operation(summary = "Export excel order revenue report")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<byte[]> exportOrderRevenueReport(@RequestParamsObject SearchRevenueReportRequest request);

    @GetMapping("/subscriber")
    @Operation(summary = "Search subscriber report")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> searchSubscriberReport(@RequestParamsObject SearchRevenueReportRequest request,
                                                    @PageableDefault Pageable pageable);

    @PostMapping("/subscriber/export-excel")
    @Operation(summary = "Export excel subscriber report")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<byte[]> exportSubscriberReport(@RequestParamsObject SearchRevenueReportRequest request);

    @GetMapping("/package")
    @Operation(summary = "Search package report")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<Object> searchPackageReport(@RequestParamsObject SearchRevenueReportRequest request,
                                                  @PageableDefault Pageable pageable);

    @PostMapping("/package/export-excel")
    @Operation(summary = "Export excel package report")
    @ApiResponse(
        responseCode = "200"
    )
    ResponseEntity<byte[]> exportPackageReport(@RequestParamsObject SearchRevenueReportRequest request);
}
