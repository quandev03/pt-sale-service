package com.vnsky.bcss.projectbase.infrastructure.secondary.external.adapter;

import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.BaseMbfResponse;
import com.vnsky.bcss.projectbase.shared.constant.IntegrationConstant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
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
    @Retryable(retryFor = {Exception.class}, backoff = @Backoff(value = 30000L), maxAttempts = 5)
    public <T extends BaseMbfResponse<?>> T executeRequestWithRetryAndErrorHandling(BaseIntegrationRequest request, Class<T> clazz) {
        log.debug("Executing request with retry and error handling for type: {}", request.getType());

        T response = this.executeRequest(request, clazz);

        // Kiểm tra nếu response có code ERROR thì throw exception để trigger retry
        if (response != null && !IntegrationConstant.SUCCESS_MESSAGE.equals(response.getCode())) {
            String errorMessage = String.format("Code: %s, Description: %s",
                response.getCode(), response.getDescription());
            log.warn("MBF response indicates error, will retry: {}", errorMessage);

            // Sử dụng error code chung cho tất cả lỗi MBF
            throw BaseException.internalServerError(ErrorCode.MBF_RESPONSE_ERROR)
                .message(errorMessage)
                .build();
        }

        log.debug("MBF response successful for type: {}", request.getType());
        return response;
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
    public <T> T executeRequest(String url, HttpMethod method, Object body, Class<T> clazz) {
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
