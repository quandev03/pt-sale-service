package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.AdvertisementDTO;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;

import java.util.List;

public interface AdvertisementServicePort {

    AdvertisementDTO create(AdvertisementDTO dto);

    AdvertisementDTO update(String id, AdvertisementDTO dto);

    void delete(String id);

    AdvertisementDTO getById(String id);

    List<AdvertisementDTO> getAll(String clientId, AdvertisementStatus status);

    List<AdvertisementDTO> getActiveAdvertisements();
}

