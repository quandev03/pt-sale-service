package com.vnsky.bcss.projectbase.wire.leader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.leader.DefaultCandidate;
import org.springframework.integration.support.leader.LockRegistryLeaderInitiator;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Configuration
public class LeaderConfig {

    private static final String REGION = "book-esim";
    private static final String ROLE = "book-esim-master";

    @Bean
    public DefaultLockRepository lockRepository(DataSource dataSource) throws UnknownHostException {
        String clientId = InetAddress.getLocalHost().getHostName() + ":" + ProcessHandle.current().pid();
        DefaultLockRepository repo = new DefaultLockRepository(dataSource, clientId);
        repo.setRegion(REGION);
        repo.setTimeToLive(30_000);
        repo.setCheckDatabaseOnStart(true);
        return repo;
    }

    @Bean
    public JdbcLockRegistry jdbcLockRegistry(LockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }

    @Bean
    public LockRegistryLeaderInitiator leaderInitiator(JdbcLockRegistry lockRegistry) throws UnknownHostException {
        var candidateId = InetAddress.getLocalHost().getHostName();
        var candidate = new DefaultCandidate(candidateId, ROLE);
        var initiator = new LockRegistryLeaderInitiator(lockRegistry, candidate);
        initiator.setHeartBeatMillis(5_000);
        initiator.setBusyWaitMillis(1_000);
        initiator.setPublishFailedEvents(true);
        initiator.setAutoStartup(true);
        return initiator;
    }
}
