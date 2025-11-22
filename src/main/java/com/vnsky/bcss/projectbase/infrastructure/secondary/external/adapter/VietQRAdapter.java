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
            VietQRRequest request = VietQRRequest.builder()
                .accountNo(accountNo)
                .accountName(accountName != null ? accountName : "")
                .acqId(bankCode)
                .amount(amount)
                .addInfo(content)
                .format("qr")
                .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<VietQRRequest> entity = new HttpEntity<>(request, headers);

            VietQRResponse response = restTemplate.postForObject(VIETQR_GENERATE_API, entity, VietQRResponse.class);
            
            if (response != null && "00".equals(response.getCode())) {
                log.info("{}QR code generated successfully", LOG_PREFIX);
                return response;
            } else {
                log.error("{}Failed to generate QR code: {}", LOG_PREFIX, response != null ? response.getDesc() : "Unknown error");
                return null;
            }
        } catch (Exception e) {
            log.error("{}Error generating QR code: {}", LOG_PREFIX, e.getMessage(), e);
            return null;
        }
    }
}

