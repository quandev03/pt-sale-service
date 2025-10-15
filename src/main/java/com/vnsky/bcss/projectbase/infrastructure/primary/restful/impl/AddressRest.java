package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.AddressServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ProvinceDto;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.AddressOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressRest implements AddressOperation {
    private final AddressServicePort addressService;

    @Override
    public ResponseEntity<Object> getProvinces() {
        return ResponseEntity.ok(addressService.getProvinces());
    }

    @Override
    public ResponseEntity<Object> getProvince(String provinceCode) {
        return ResponseEntity.ok(addressService.getCommunes(provinceCode));
    }
}
