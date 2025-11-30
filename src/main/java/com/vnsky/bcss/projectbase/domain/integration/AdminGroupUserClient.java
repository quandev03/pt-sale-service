package com.vnsky.bcss.projectbase.domain.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Call admin API to register/unregister users with permission groups when partners buy packages.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminGroupUserClient {

    private final RestTemplate restTemplate;

    @Value("${third-party.admin.group-user-url:}")
    private String groupUserUrl;

    /**
     * Default to empty so deployments must explicitly set to the provided api key (qlpt).
     */
    @Value("${third-party.admin.group-user-api-key:}")
    private String groupUserApiKey;

    public void addUserToGroup(String groupId, String userId) {
        execute(HttpMethod.POST, groupId, userId);
    }

    public void removeUserFromGroup(String groupId, String userId) {
        execute(HttpMethod.DELETE, groupId, userId);
    }

    private void execute(HttpMethod method, String groupId, String userId) {
        if (!StringUtils.hasText(groupUserUrl) || !StringUtils.hasText(groupUserApiKey)) {
            log.debug("Skip syncing group-user because URL or API key is not configured");
            return;
        }

        if (!StringUtils.hasText(groupId) || !StringUtils.hasText(userId)) {
            log.warn("Skip syncing group-user because groupId={} or userId={} is blank", groupId, userId);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", groupUserApiKey);

        GroupUserPayload payload = new GroupUserPayload(groupId, userId);

        try {
            restTemplate.exchange(groupUserUrl, method, new HttpEntity<>(payload, headers), Void.class);
        } catch (Exception ex) {
            log.error("Failed to sync group-user mapping. method={}, groupId={}, userId={}", method, groupId, userId, ex);
        }
    }

    private record GroupUserPayload(String groupId, String userId) { }
}


