package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.infrastructure.data.response.BankResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankService {

    private final RestTemplate restTemplate;
    private static final String VIETQR_BANKS_API = "https://api.vietqr.io/v2/banks";
    private static final String LOG_PREFIX = "[BankService]_";

    @Cacheable(value = "banks", unless = "#result == null || #result.isEmpty()")
    public List<BankResponse> getAllBanks() {
        log.info("{}Fetching banks from VietQR API", LOG_PREFIX);
        try {
            Map<String, Object> response = restTemplate.getForObject(VIETQR_BANKS_API, Map.class);
            
            if (response != null && response.containsKey("data")) {
                Object data = response.get("data");
                
                if (data instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> banksList = (List<Map<String, Object>>) data;
                    return banksList.stream()
                        .map(this::mapToBankResponse)
                        .toList();
                }
            }
            
            log.warn("{}Unexpected response format from VietQR API", LOG_PREFIX);
            return List.of();
        } catch (Exception e) {
            log.error("{}Error fetching banks from VietQR API: {}", LOG_PREFIX, e.getMessage(), e);
            return List.of();
        }
    }

    private BankResponse mapToBankResponse(Map<String, Object> bankMap) {
        return BankResponse.builder()
            .code((String) bankMap.get("code"))
            .name((String) bankMap.get("name"))
            .shortName((String) bankMap.get("shortName"))
            .logo((String) bankMap.get("logo"))
            .nameEn((String) bankMap.get("nameEn"))
            .build();
    }
}

