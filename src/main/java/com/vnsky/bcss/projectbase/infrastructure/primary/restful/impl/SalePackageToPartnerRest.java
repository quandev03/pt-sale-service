package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.SalePackageToPartnerOperation;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SalePackageToPartnerRest implements SalePackageToPartnerOperation {
    private final PartnerPackageSubscriptionServicePort partnerPackageSubscriptionServicePort;

    @Override
    public ResponseEntity<Object> create(PartnerPackageSubscriptionDTO request) {
        return ResponseEntity.ok(partnerPackageSubscriptionServicePort.buyPackage(request));
    }

    @Override
    public ResponseEntity<Object> list(String packageProfileId,
                                       PartnerPackageSubscriptionStatus status, Pageable pageable) {
        return ResponseEntity.ok(partnerPackageSubscriptionServicePort.getPartner(packageProfileId, status, pageable));
    }
}
