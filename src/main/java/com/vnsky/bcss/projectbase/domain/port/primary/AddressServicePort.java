package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.infrastructure.data.response.CommuneDto;
import com.vnsky.bcss.projectbase.infrastructure.data.response.CommuneResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ProvinceDto;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ProvincesResponse;

import java.util.List;

public interface AddressServicePort {
    ProvincesResponse getProvinces();
    CommuneResponse getCommunes(String provinceCode);
}
