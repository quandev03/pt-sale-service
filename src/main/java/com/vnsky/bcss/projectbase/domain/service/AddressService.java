package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.port.primary.AddressServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.CommuneDto;
import com.vnsky.bcss.projectbase.infrastructure.data.response.CommuneResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ProvinceDto;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ProvincesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressService implements AddressServicePort {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String GET_ADDRESS_URL="https://production.cas.so/address-kit/2025-07-01/provinces";
    private static final String GET_COMMUNES_URL_TEMPLATE="https://production.cas.so/address-kit/2025-07-01/provinces/%s/communes";


    @Override
    public ProvincesResponse getProvinces() {
        return restTemplate.getForObject(GET_ADDRESS_URL, ProvincesResponse.class);
    }

    @Override
    public CommuneResponse getCommunes(String provinceCode) {
        String url = String.format(GET_COMMUNES_URL_TEMPLATE, provinceCode);
        return restTemplate.getForObject(url, com.vnsky.bcss.projectbase.infrastructure.data.response.CommuneResponse.class);
    }
}
