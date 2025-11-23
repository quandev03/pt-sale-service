package com.vnsky.bcss.projectbase.infrastructure.secondary.external.adapter;

import com.vnsky.bcss.projectbase.domain.port.secondary.external.VietQRPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.VietQRRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.VietQRResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class VietQRAdapter implements VietQRPort {

    private final RestTemplate restTemplate;
    private static final String VIETQR_GENERATE_API = "https://api.vietqr.io/v2/generate";
    private static final String LOG_PREFIX = "[VietQRAdapter]_";

    @Override
    public VietQRResponse generateQRCode(String accountNo, String accountName, BigDecimal amount, String content, String bankCode) {
        log.info("{}Generating QR code for account: {}, amount: {}, bank: {}", LOG_PREFIX, accountNo, amount, bankCode);
        
        try {
            // Kiểm tra bankCode
            if (bankCode == null || bankCode.isEmpty()) {
                log.error("{}Bank code is null or empty, cannot generate QR code", LOG_PREFIX);
                return null;
            }
            
            VietQRRequest request = VietQRRequest.builder()
                .accountNo(accountNo)
                .accountName(accountName != null ? accountName : "")
                .acqId(bankCode)
                .amount(amount)
                .addInfo(content)
                .format("qr")
                .build();

            log.info("{}VietQR request: accountNo={}, acqId={}, amount={}, addInfo={}", 
                LOG_PREFIX, accountNo, bankCode, amount, content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<VietQRRequest> entity = new HttpEntity<>(request, headers);

            VietQRResponse response = restTemplate.postForObject(VIETQR_GENERATE_API, entity, VietQRResponse.class);
            
            log.info("{}VietQR API response: code={}, desc={}, data={}", LOG_PREFIX, 
                response != null ? response.getCode() : "null",
                response != null ? response.getDesc() : "null",
                response != null && response.getData() != null ? response.getData().getQrDataURL() : "null");
            
            if (response != null && "00".equals(response.getCode())) {
                if (response.getData() != null && response.getData().getQrDataURL() != null) {
                    log.info("{}QR code generated successfully, URL: {}", LOG_PREFIX, response.getData().getQrDataURL());
                } else {
                    log.warn("{}QR code generated but data or URL is null", LOG_PREFIX);
                }
                return response;
            } else {
                log.error("{}Failed to generate QR code: code={}, desc={}", LOG_PREFIX, 
                    response != null ? response.getCode() : "null",
                    response != null ? response.getDesc() : "Unknown error");
                return null;
            }
        } catch (Exception e) {
            log.error("{}Error generating QR code: {}", LOG_PREFIX, e.getMessage(), e);
            return null;
        }
    }
}

