package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionCreateCommand;
import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PartnerPackageSubscriptionCreateRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.PartnerPackageSubscriptionOperation;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PartnerPackageSubscriptionRest implements PartnerPackageSubscriptionOperation {

    private final PartnerPackageSubscriptionServicePort subscriptionServicePort;

    @Override
    public ResponseEntity<Object> createSubscription(@Valid PartnerPackageSubscriptionCreateRequest request) {
        var command = PartnerPackageSubscriptionCreateCommand.builder()
            .organizationUnitId(request.getOrganizationUnitId())
            .packageProfileId(request.getPackageProfileId())
            .startTime(request.getStartTime())
            .build();
        return ResponseEntity.ok(subscriptionServicePort.createSubscription(command));
    }

    @Override
    public ResponseEntity<Object> listSubscriptions(String organizationUnitId,
                                                    String packageProfileId,
                                                    PartnerPackageSubscriptionStatus status,
                                                    Pageable pageable) {
        return ResponseEntity.ok(subscriptionServicePort.listSubscriptions(organizationUnitId, packageProfileId, status, pageable));
    }

    @Override
    public ResponseEntity<Object> stopSubscription(String id) {
        return ResponseEntity.ok(subscriptionServicePort.stopSubscription(id));
    }
}

