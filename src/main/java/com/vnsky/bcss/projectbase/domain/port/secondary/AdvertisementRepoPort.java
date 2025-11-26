package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.AdvertisementDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdvertisementRepoPort {

    AdvertisementDTO save(AdvertisementDTO dto);

    Optional<AdvertisementDTO> findById(String id);

    Optional<AdvertisementDTO> findByIdAndClientId(String id, String clientId);

    void deleteById(String id);

    List<AdvertisementDTO> findByClientId(String clientId);

    List<AdvertisementDTO> findActiveAdvertisements(LocalDateTime currentDate);
}


