package com.vnsky.bcss.projectbase.wire.leader;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationLineDTO;
import com.vnsky.bcss.projectbase.domain.dto.IncompleteRegistrationDTO;
import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.EsimRegistrationLineRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.StockIsdnRepoPort;
import com.vnsky.bcss.projectbase.domain.service.BookEsimService;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.OrganizationUnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEsimWorker {

    private static final String WORKER = "book-esim-worker";

    private final OrganizationUnitRepository organizationUnitRepository;
    private final EsimRegistrationLineRepoPort esimRegistrationLineRepoPort;
    private final StockIsdnRepoPort stockIsdnRepoPort;
    private final TaskExecutor taskExecutor;
    private final BookEsimServicePort bookEsimServicePort;
    private final ApplicationContext applicationContext;

    private static final String SYSTEM = "system";

    public void process(String batchId) {
        log.info("[{}] Scanning for incomplete eSIM registrations...", WORKER);
        long batchStart = System.currentTimeMillis();
        try {
//            String organizationId = this.organizationUnitServicePort.getOrgCurrent().getId();
//            String currentClientId = SecurityUtil.getCurrentClientId();
//            String currentUsername = SecurityUtil.getCurrentUsername();

            // Tiếp tục tiến trình bookEsim với esimRegistrationLine chưa hoàn thành
//            List<EsimRegistrationLineDTO> esimRegistrationLines = this.esimRegistrationLineRepoPort.findIncompleteRegistrationLines();
//            Map<String, List<EsimRegistrationLineDTO>> esimRegistrationLinesMap =
//                esimRegistrationLines.stream()
//                    .collect(Collectors.groupingBy(EsimRegistrationLineDTO::getEsimRegistrationId));

            // Tiếp tục tiến trình bookEsim với số lượng còn thiếu
            List<IncompleteRegistrationDTO> pendingRegistrations = bookEsimServicePort.findIncompleteRegistrations();

            if (pendingRegistrations.isEmpty()) {
                log.info("[{}] No pending registrations", WORKER);
                return;
            }
            log.info("[{}] Found {} registrations", WORKER, pendingRegistrations.size());

            pendingRegistrations.forEach(reg -> {
                String orgId = organizationUnitRepository.findOrganizationIdByEsimRegistration(reg.getRegistrationId());
                String clientId = organizationUnitRepository.findById(orgId).orElseThrow().getClientId();
                log.info("[{}] Submitting registrationId={} clientId={} with {} requests (orgId={}, createdBy={})",
                    WORKER, reg.getRegistrationId(), clientId, reg.getRequest().size(), orgId, SYSTEM);
                BookEsimService self = applicationContext.getBean(BookEsimService.class);
                self.validateBeforeCreatingSaleOrder(reg.getRequest());

                Long estimatedDebit = self.estimateTotalDebit(reg.getRequest());
                self.checkOrganizationUnitLimit(estimatedDebit, orgId);

                taskExecutor.execute(new DelegatingSecurityContextRunnable(() -> {
                    try {
                        log.debug("[{}] Start processing registrationId={} ...", WORKER, reg.getRegistrationId());
                        // Get ISDNs from registration lines where serial is null (incomplete processing)
                        List<EsimRegistrationLineDTO> registrationLines = esimRegistrationLineRepoPort.findByEsimRegistrationIdAndSerialIsNull(reg.getRegistrationId());
                        List<StockIsdnDTO> reservedIsdns = registrationLines.stream()
                            .map(line -> stockIsdnRepoPort.findByIsdn(line.getIsdn()).orElse(null))
                            .filter(Objects::nonNull)
                            .toList();

                        self.processExistingRegistrationInBackground(
                            reg.getRegistrationId(),
                            reg.getRequest(),
                            clientId,
                            SYSTEM,
                            orgId,
                            reservedIsdns
                        );
                        log.debug("[{}] Done processing registrationId={}", WORKER, reg.getRegistrationId());
                    } catch (Exception ex) {
                        log.error("[{}] Failed processing registrationId={}: {}", WORKER, reg.getRegistrationId(), ex.getMessage(), ex);
                    }
                }));
            });

        } catch (Exception e) {
            long took = System.currentTimeMillis() - batchStart;
            log.error("[{}] Batch {} failed after {} ms: {}", WORKER, batchId, took, e.getMessage(), e);
        }
    }
}
