package com.vnsky.bcss.projectbase.domain.port.secondary.external;

import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.BaseMbfResponse;
import org.springframework.http.HttpMethod;

public interface IntegrationPort {
    <T> T executeRequest(BaseIntegrationRequest request, Class<T> clazz);

    <T> T executeRequestWithRetry(BaseIntegrationRequest request, Class<T> clazz);

    /**
     * Thực hiện request với retry logic khi response có code ERROR từ BaseMbfResponse
     * @param request Request để gửi
     * @param clazz Class type của response
     * @param <T> Type extends BaseMbfResponse
     * @return Response sau khi retry thành công
     */
    <T extends BaseMbfResponse<?>> T executeRequestWithRetryAndErrorHandling(BaseIntegrationRequest request, Class<T> clazz);

    BaseIntegrationRequest buildIntegrationRequest(String cmd, String type, Object extraInfo, Object data);

    <T> T executeRequest(String url, HttpMethod method, Object body, Class<T> clazz);
}
