package com.vnsky.bcss.projectbase.wire.scheduler;

import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerPackageSubscriptionScheduler {

    private final PartnerPackageSubscriptionServicePort subscriptionServicePort;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void expireSubscriptions() {
        int processed = subscriptionServicePort.expireSubscriptions();
        if (processed > 0) {
            log.info("Auto expired {} partner package subscriptions", processed);
        }
    }
}








