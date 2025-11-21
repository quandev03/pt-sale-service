package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionCreateCommand;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionRepoPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerPackageSubscriptionServiceTest {

    @Mock
    private PartnerPackageSubscriptionRepoPort subscriptionRepoPort;

    @Mock
    private OrganizationUnitRepoPort organizationUnitRepoPort;

    @Mock
    private PackageProfileRepoPort packageProfileRepoPort;

    @InjectMocks
    private PartnerPackageSubscriptionService service;

    @Test
    void createSubscription_shouldCalculateEndTimeByDayCycle() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 8, 0);
        OrganizationUnitDTO org = OrganizationUnitDTO.builder()
            .id("ORG-1")
            .orgCode("ORG01")
            .orgName("Org name")
            .status(Status.ACTIVE.getValue())
            .contractDate(LocalDate.now())
            .build();

        PackageProfileDTO packageProfile = PackageProfileDTO.builder()
            .id("PKG-1")
            .cycleUnit(0)
            .cycleValue(10)
            .status(Status.ACTIVE.getValue())
            .build();

        when(organizationUnitRepoPort.findById("ORG-1")).thenReturn(Optional.of(org));
        when(packageProfileRepoPort.findById("PKG-1")).thenReturn(packageProfile);
        when(subscriptionRepoPort.findByOrgUnitAndPackageAndStatuses(anyString(), anyString(), anyList()))
            .thenReturn(Optional.empty());
        when(subscriptionRepoPort.saveAndFlush(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var command = PartnerPackageSubscriptionCreateCommand.builder()
            .organizationUnitId("ORG-1")
            .packageProfileId("PKG-1")
            .startTime(start)
            .build();

        PartnerPackageSubscriptionDTO result = service.createSubscription(command);

        assertThat(result.getEndTime()).isEqualTo(start.plusDays(10));
        assertThat(result.getStatus()).isEqualTo(PartnerPackageSubscriptionStatus.ACTIVE);
    }

    @Test
    void expireSubscriptions_shouldMarkExpiredAndPersist() {
        PartnerPackageSubscriptionDTO dto = PartnerPackageSubscriptionDTO.builder()
            .id("SUB-1")
            .organizationUnitId("ORG")
            .packageProfileId("PKG")
            .startTime(LocalDateTime.now().minusDays(10))
            .endTime(LocalDateTime.now().minusDays(1))
            .status(PartnerPackageSubscriptionStatus.ACTIVE)
            .build();

        when(subscriptionRepoPort.findActiveSubscriptionsEndingBefore(any())).thenReturn(List.of(dto));
        when(subscriptionRepoPort.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        int processed = service.expireSubscriptions();

        ArgumentCaptor<PartnerPackageSubscriptionDTO> captor = ArgumentCaptor.forClass(PartnerPackageSubscriptionDTO.class);
        verify(subscriptionRepoPort).saveAndFlush(captor.capture());
        assertThat(processed).isEqualTo(1);
        assertThat(captor.getValue().getStatus()).isEqualTo(PartnerPackageSubscriptionStatus.EXPIRED);
    }
}


