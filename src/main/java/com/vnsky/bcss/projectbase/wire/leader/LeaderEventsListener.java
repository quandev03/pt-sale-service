package com.vnsky.bcss.projectbase.wire.leader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.integration.leader.event.AbstractLeaderEvent;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaderEventsListener implements ApplicationListener<AbstractLeaderEvent> {

    private final BookEsimWorker bookEsimWorker;
    private boolean RUN_ONCE = true;

    @Getter
    private volatile String role = "unknown";

    @Override
    public void onApplicationEvent(AbstractLeaderEvent event) {
        this.role = event.getRole();

        if (event instanceof OnGrantedEvent && RUN_ONCE) {
            log.info("[leader] Granted |  role={}", role);

            String batchId = UUID.randomUUID().toString().replace("-", "");
            log.info("[leader] Instance is leader, starting batch {}", batchId);
            bookEsimWorker.process(batchId);
            RUN_ONCE = false;
        } else if (event instanceof OnRevokedEvent) {
            log.debug("[leader] Revoked |  role={}", role);
        } else {
            log.debug("[leader] Event={} |  role={}", event.getClass().getSimpleName(), role);
        }
    }
}
