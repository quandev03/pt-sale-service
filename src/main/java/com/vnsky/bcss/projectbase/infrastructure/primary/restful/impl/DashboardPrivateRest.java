package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.SaleOrderServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.StockIsdnServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.DashboardPrivateOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardPrivateRest implements DashboardPrivateOperation {
    private final StockIsdnServicePort stockIsdnServicePort;
    private final PackageManagerServicePort packageManagerServicePort;
    private final SaleOrderServicePort saleOrderServicePort;

    @Override
    public ResponseEntity<Object> totalEsimProcured() {
        return ResponseEntity.ok(stockIsdnServicePort.totalEsimProcured(null));
    }

    @Override
    public ResponseEntity<Object> totalEsimSold() {
        return ResponseEntity.ok(stockIsdnServicePort.totalEsimSold(null));
    }

    @Override
    public ResponseEntity<Object> totalSTBCalled900() {
        return ResponseEntity.ok(stockIsdnServicePort.totalSTBCalled900(null));
    }

    @Override
    public ResponseEntity<Object> totalPackagesSold() {
        return ResponseEntity.ok(packageManagerServicePort.totalPackagesSold(null));
    }

    @Override
    public ResponseEntity<Object> statisticEsimSold(String startDate, String endDate, int granularity) {
        return ResponseEntity.ok(saleOrderServicePort.statisticEsimSold(null, startDate, endDate, granularity));
    }

    @Override
    public ResponseEntity<Object> statisticPackagesSold(String startDate, String endDate, int granularity) {
        return ResponseEntity.ok(packageManagerServicePort.statisticPackagesSold(null, startDate, endDate, granularity));
    }

    @Override
    public ResponseEntity<Object> statisticEsimSoldOrg(String startDate, String endDate) {
        return ResponseEntity.ok(saleOrderServicePort.statisticEsimSoldOrg(null, startDate, endDate));
    }

    @Override
    public ResponseEntity<Object> statisticPackagesSoldOrg(String startDate, String endDate) {
        return ResponseEntity.ok(packageManagerServicePort.statisticPackagesSoldOrg(null, startDate, endDate));
    }
}
