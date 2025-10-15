package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.infrastructure.data.response.ProvinceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("${application.path.base.public}/address")
public interface AddressOperation {
    @GetMapping
    ResponseEntity<Object> getProvinces();

    @GetMapping("/{provinceCode}")
    ResponseEntity<Object> getProvince(@PathVariable String provinceCode);
}
