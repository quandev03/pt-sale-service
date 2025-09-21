package com.vnsky.bcss.projectbase.domain.port.secondary.external;

import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import org.springframework.http.HttpMethod;

public interface IntegrationPort {
    <T> T executeRequest(BaseIntegrationRequest request, Class<T> clazz);

    <T> T executeRequestWithRetry(BaseIntegrationRequest request, Class<T> clazz);

    BaseIntegrationRequest buildIntegrationRequest(String cmd, String type, Object extraInfo, Object data);

    <T> T excuteRequest(String url, HttpMethod method, Object body, Class<T> clazz);
}
