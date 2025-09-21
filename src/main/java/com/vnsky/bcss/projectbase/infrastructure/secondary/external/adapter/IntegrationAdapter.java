package com.vnsky.bcss.projectbase.infrastructure.secondary.external.adapter;

import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationAdapter implements IntegrationPort {

    @Value("${third-party.integration.apikey}")
    private String apiKey;

    @Value("${third-party.integration.general}")
    private String generalUrl;

    private static final String API_KEY ="api-key";

    private final RestOperations restTemplate;

    @Override
    public <T> T executeRequest(BaseIntegrationRequest request, Class<T> clazz) {
        HttpHeaders httpHeaders = getHeader();
        HttpEntity<BaseIntegrationRequest> requestEntity = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<T> responseEntity = restTemplate
            .exchange(generalUrl, HttpMethod.POST, requestEntity, clazz);
        return responseEntity.getBody();
    }

    @Override
    @Retryable(retryFor = {Exception.class}, backoff = @Backoff(value = 120000L), maxAttempts = 5)
    public <T> T executeRequestWithRetry(BaseIntegrationRequest request, Class<T> clazz) {
        return this.executeRequest(request, clazz);
    }

    @Override
    public BaseIntegrationRequest buildIntegrationRequest(String cmd, String type, Object extraInfo, Object data) {
        return BaseIntegrationRequest.builder()
            .cmd(cmd)
            .type(type)
            .extraInfo(extraInfo)
            .data(data)
            .build();
    }

    @Override
    public <T> T excuteRequest(String url, HttpMethod method, Object body, Class<T> clazz) {
        HttpHeaders httpHeaders = getHeader();
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.exchange(url, method, requestEntity, clazz).getBody();
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(API_KEY, apiKey);
        httpHeaders.add(HttpHeaders.ACCEPT_LANGUAGE, "en");
        return httpHeaders;
    }
}
