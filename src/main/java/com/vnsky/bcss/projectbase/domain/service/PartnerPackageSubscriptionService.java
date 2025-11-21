package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionCreateCommand;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionView;
import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionRepoPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerPackageSubscriptionService implements PartnerPackageSubscriptionServicePort {

    private final PartnerPackageSubscriptionRepoPort subscriptionRepoPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;
    private final PackageProfileRepoPort packageProfileRepoPort;

    @Override
    @Transactional
    public PartnerPackageSubscriptionDTO createSubscription(PartnerPackageSubscriptionCreateCommand command) {
        OrganizationUnitDTO organizationUnit = organizationUnitRepoPort.findById(command.getOrganizationUnitId())
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED).build());

        if (!Objects.equals(organizationUnit.getStatus(), Status.ACTIVE.getValue())) {
            throw BaseException.badRequest(ErrorCode.ORG_NOT_EXISTED)
                .message("Đơn vị không hoạt động hoặc không tồn tại")
                .build();
        }

        PackageProfileDTO packageProfile = packageProfileRepoPort.findByPackageCode(command.getPackageProfileId()));

        if (!Objects.equals(packageProfile.getStatus(), Status.ACTIVE.getValue())) {
            throw BaseException.badRequest(ErrorCode.PACKAGE_PROFILE_INVALID)
                .message("Gói cước chưa được bật trạng thái hoạt động")
                .build();
        }

        subscriptionRepoPort.findByOrgUnitAndPackageAndStatuses(
                organizationUnit.getId(),
                packageProfile.getId(),
                List.of(PartnerPackageSubscriptionStatus.ACTIVE, PartnerPackageSubscriptionStatus.PENDING_PAYMENT)
            )
            .ifPresent(existing -> {
                throw BaseException.badRequest(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_DUPLICATED)
                    .message("Đối tác đã có gói này đang hoạt động")
                    .build();
            });

        LocalDateTime startTime = Optional.ofNullable(command.getStartTime()).orElse(LocalDateTime.now());
        LocalDateTime endTime = calculateEndTime(startTime, packageProfile.getCycleValue(), packageProfile.getCycleUnit());

        PartnerPackageSubscriptionDTO dto = PartnerPackageSubscriptionDTO.builder()
            .organizationUnitId(organizationUnit.getId())
            .packageProfileId(packageProfile.getId())
            .startTime(startTime)
            .endTime(endTime)
            .status(PartnerPackageSubscriptionStatus.ACTIVE)
            .build();

        return subscriptionRepoPort.saveAndFlush(dto);
    }

    @Override
    public Page<PartnerPackageSubscriptionView> listSubscriptions(String organizationUnitId,
                                                                  String packageProfileId,
                                                                  PartnerPackageSubscriptionStatus status,
                                                                  Pageable pageable) {
        String statusValue = status != null ? status.name() : null;
        return subscriptionRepoPort.search(organizationUnitId, packageProfileId, statusValue, pageable);
    }

    @Override
    @Transactional
    public PartnerPackageSubscriptionDTO stopSubscription(String subscriptionId) {
        PartnerPackageSubscriptionDTO subscription = subscriptionRepoPort.findById(subscriptionId)
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_NOT_FOUND).build());

        if (!PartnerPackageSubscriptionStatus.ACTIVE.equals(subscription.getStatus())) {
            throw BaseException.badRequest(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_ALREADY_STOPPED)
                .message("Gói cước đã dừng hoặc hết hạn")
                .build();
        }

        subscription.setStatus(PartnerPackageSubscriptionStatus.INACTIVE);
        subscription.setEndTime(LocalDateTime.now());
        return subscriptionRepoPort.saveAndFlush(subscription);
    }

    @Override
    @Transactional
    public int expireSubscriptions() {
        List<PartnerPackageSubscriptionDTO> expired = subscriptionRepoPort.findActiveSubscriptionsEndingBefore(LocalDateTime.now());
        expired.forEach(subscription -> subscription.setStatus(PartnerPackageSubscriptionStatus.EXPIRED));
        expired.forEach(subscriptionRepoPort::saveAndFlush);
        if (!expired.isEmpty()) {
            log.info("Expired {} partner package subscriptions", expired.size());
        }
        return expired.size();
    }

    private LocalDateTime calculateEndTime(LocalDateTime startTime, Integer cycleValue, Integer cycleUnit) {
        if (cycleValue == null || cycleUnit == null || cycleValue <= 0) {
            throw BaseException.badRequest(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_INVALID_CYCLE)
                .message("Chu kỳ gói cước không hợp lệ")
                .build();
        }

        return switch (cycleUnit) {
            case 0 -> startTime.plusDays(cycleValue.longValue());
            case 1 -> startTime.plusMonths(cycleValue.longValue());
            default -> throw BaseException.badRequest(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_INVALID_CYCLE)
                .message("Đơn vị chu kỳ không hỗ trợ")
                .build();
        };
    }
}


