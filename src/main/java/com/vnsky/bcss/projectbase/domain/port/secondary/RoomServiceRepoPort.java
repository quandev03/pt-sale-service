package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.RoomServiceDTO;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;

import java.util.List;
import java.util.Optional;

public interface RoomServiceRepoPort {

    RoomServiceDTO save(RoomServiceDTO dto);

    RoomServiceDTO update(RoomServiceDTO dto);

    Optional<RoomServiceDTO> findById(String id);

    Optional<RoomServiceDTO> findByClientIdAndServiceCode(String clientId, String serviceCode);

    Optional<RoomServiceDTO> findByClientIdAndServiceCodeExcludingId(String clientId, String serviceCode, String id);

    List<RoomServiceDTO> findByOrgUnitId(String orgUnitId);

    List<RoomServiceDTO> findByClientId(String clientId);

    List<RoomServiceDTO> findByFilters(String clientId, String orgUnitId, RoomServiceType serviceType, Integer status);

    void delete(String id);
}

