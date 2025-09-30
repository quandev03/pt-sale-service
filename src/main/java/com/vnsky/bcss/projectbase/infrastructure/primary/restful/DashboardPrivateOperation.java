package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Dashboard", description = "api Dashboard")
@RequestMapping("${application.path.base.private}/dashboard")
public interface DashboardPrivateOperation {

    @Operation(summary = "Tổng eSim đã đặt")
    @GetMapping("/total-esim-procured")
    ResponseEntity<Object> totalEsimProcured();

    @Operation(summary = "Tổng eSim đã bán")
    @GetMapping("/total-esim-sold")
    ResponseEntity<Object> totalEsimSold();

    @Operation(summary = "Tổng STB đã gọi 900")
    @GetMapping("/total-stb-called-900")
    ResponseEntity<Object> totalSTBCalled900();

    @Operation(summary = "Tổng gói cước đã bán")
    @GetMapping("/total-packages-sold")
    ResponseEntity<Object> totalPackagesSold();

    @Operation(summary = "Số lượng eSim đã bán")
    @GetMapping("/statistic/esim-sold")
    ResponseEntity<Object> statisticEsimSold(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) int granularity);

    @Operation(summary = "Số lượng gói cước đã bán")
    @GetMapping("/statistic/packages-sold")
    ResponseEntity<Object> statisticPackagesSold(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) int granularity);

    @Operation(summary = "Số lượng eSim đã bán (mỗi Đại lý)")
    @GetMapping("/statistic/esim-sold/org")
    ResponseEntity<Object> statisticEsimSoldOrg(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate);

    @Operation(summary = "Số lượng gói cước đã bán (mỗi Đại lý)")
    @GetMapping("/statistic/packages-sold/org")
    ResponseEntity<Object> statisticPackagesSoldOrg(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate);
}
