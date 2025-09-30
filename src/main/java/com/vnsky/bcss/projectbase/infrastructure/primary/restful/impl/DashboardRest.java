package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.SaleOrderServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.StockIsdnServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.DashboardOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class DashboardRest implements DashboardOperation {
    private final StockIsdnServicePort stockIsdnServicePort;
    private final OrganizationUnitServicePort organizationUnitServicePort;
    private final PackageManagerServicePort packageManagerServicePort;
    private final SaleOrderServicePort saleOrderServicePort;

    @Override
    public ResponseEntity<Object> totalEsimProcured() {
        return ResponseEntity.ok(stockIsdnServicePort.totalEsimProcured(organizationUnitServicePort.getOrgCurrent().getOrgCode()));
    }

    @Override
    public ResponseEntity<Object> totalEsimSold() {
        return ResponseEntity.ok(stockIsdnServicePort.totalEsimSold(organizationUnitServicePort.getOrgCurrent().getOrgCode()));
    }

    @Override
    public ResponseEntity<Object> totalSTBCalled900() {
        return ResponseEntity.ok(stockIsdnServicePort.totalSTBCalled900(organizationUnitServicePort.getOrgCurrent().getOrgCode()));
    }

    @Override
    public ResponseEntity<Object> totalPackagesSold() {
        return ResponseEntity.ok(packageManagerServicePort.totalPackagesSold(organizationUnitServicePort.getOrgCurrent().getOrgCode()));
    }

    @Override
    public ResponseEntity<Object> statisticEsimSold(String startDate, String endDate, int granularity) {
        return ResponseEntity.ok(saleOrderServicePort.statisticEsimSold(organizationUnitServicePort.getOrgCurrent().getOrgCode(), startDate, endDate, granularity));
    }

    @Override
    public ResponseEntity<Object> statisticPackagesSold(String startDate, String endDate, int granularity) {
        return ResponseEntity.ok(packageManagerServicePort.statisticPackagesSold(organizationUnitServicePort.getOrgCurrent().getOrgCode(), startDate, endDate, granularity));
    }

    @Override
    public ResponseEntity<Object> statisticEsimSoldOrg(String startDate, String endDate) {
        return ResponseEntity.ok(saleOrderServicePort.statisticEsimSoldOrg(organizationUnitServicePort.getOrgCurrent().getOrgCode(), startDate, endDate));
    }

    @Override
    public ResponseEntity<Object> statisticPackagesSoldOrg(String startDate, String endDate) {
        return ResponseEntity.ok(packageManagerServicePort.statisticPackagesSoldOrg(organizationUnitServicePort.getOrgCurrent().getOrgCode(), startDate, endDate));
    }

    @Override
    public ResponseEntity<Object> revenue() {
        return ResponseEntity.ok(saleOrderServicePort.revenue(organizationUnitServicePort.getOrgCurrent().getOrgCode()));
    }
}
