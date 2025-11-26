package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionCreateCommand;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionPaymentCommand;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionView;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionPaymentServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PartnerPackageSubscriptionCreateRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PartnerPackageSubscriptionPaymentRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.PartnerPackageSubscriptionBuyRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.PartnerPackageSubscriptionPaymentResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.PartnerPackageSubscriptionOperation;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PartnerPackageSubscriptionRest implements PartnerPackageSubscriptionOperation {

    private final PartnerPackageSubscriptionServicePort subscriptionServicePort;
    private final PartnerPackageSubscriptionPaymentServicePort paymentServicePort;
    private final OrganizationUnitServicePort organizationUnitServicePort;
    private final PackageManagerServicePort packageManagerServicePort;

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
    public ResponseEntity<Object> buySubscription(@Valid PartnerPackageSubscriptionBuyRequest request) {
        OrganizationUnitDTO orgCurrent = organizationUnitServicePort.getOrgCurrent();
        var command = PartnerPackageSubscriptionCreateCommand.builder()
            .organizationUnitId(orgCurrent.getId())
            .packageProfileId(request.getPackageProfileId())
            .startTime(request.getStartTime())
            .build();
        return ResponseEntity.ok(subscriptionServicePort.createSubscription(command));
    }

    @Override
    public ResponseEntity<Object> createSubscriptionPayment(@Valid PartnerPackageSubscriptionPaymentRequest request) {
        var command = PartnerPackageSubscriptionPaymentCommand.builder()
            .organizationUnitId(request.getOrganizationUnitId())
            .packageProfileId(request.getPackageProfileId())
            .startTime(request.getStartTime())
            .clientIp(request.getClientIp())
            .returnUrl(request.getReturnUrl())
            .build();

        var result = paymentServicePort.initiatePayment(command);
        var response = PartnerPackageSubscriptionPaymentResponse.builder()
            .subscriptionId(result.getSubscriptionId())
            .paymentId(result.getPaymentId())
            .txnRef(result.getTxnRef())
            .amount(result.getAmount())
            .orderInfo(result.getOrderInfo())
            .paymentUrl(result.getPaymentUrl())
            .status(result.getStatus() != null ? result.getStatus() : PartnerPackageSubscriptionPaymentStatus.PENDING)
            .build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> buySubscriptionPayment(@Valid PartnerPackageSubscriptionBuyRequest request) {
        OrganizationUnitDTO orgCurrent = organizationUnitServicePort.getOrgCurrent();
        var command = PartnerPackageSubscriptionPaymentCommand.builder()
            .organizationUnitId(orgCurrent.getId())
            .packageProfileId(request.getPackageProfileId())
            .startTime(request.getStartTime())
            .build();

        var result = paymentServicePort.initiatePayment(command);
        var response = PartnerPackageSubscriptionPaymentResponse.builder()
            .subscriptionId(result.getSubscriptionId())
            .paymentId(result.getPaymentId())
            .txnRef(result.getTxnRef())
            .amount(result.getAmount())
            .orderInfo(result.getOrderInfo())
            .paymentUrl(result.getPaymentUrl())
            .status(result.getStatus() != null ? result.getStatus() : PartnerPackageSubscriptionPaymentStatus.PENDING)
            .build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> listSubscriptions(String organizationUnitId,
                                                    String packageProfileId,
                                                    PartnerPackageSubscriptionStatus status,
                                                    Pageable pageable) {
        return ResponseEntity.ok(subscriptionServicePort.listSubscriptions(organizationUnitId, packageProfileId, status, pageable));
    }

    @Override
    public ResponseEntity<Object> listMySubscriptions(String packageProfileId,
                                                      PartnerPackageSubscriptionStatus status,
                                                      Pageable pageable) {
        OrganizationUnitDTO orgCurrent = organizationUnitServicePort.getOrgCurrent();
        Page<PartnerPackageSubscriptionView> page = subscriptionServicePort.listSubscriptions(
            orgCurrent.getId(),
            packageProfileId,
            status,
            pageable
        );
        return ResponseEntity.ok(page);
    }

    @Override
    public ResponseEntity<Object> listAvailablePackages() {
        java.util.List<PackageProfileDTO> packages = packageManagerServicePort.getListPackageProfile();
        return ResponseEntity.ok(packages);
    }

    @Override
    public ResponseEntity<Object> stopSubscription(String id) {
        return ResponseEntity.ok(subscriptionServicePort.stopSubscription(id));
    }
}


