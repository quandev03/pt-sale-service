package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.RoomServiceDTO;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;

import java.util.List;

public interface RoomServiceServicePort {

    RoomServiceDTO create(RoomServiceDTO dto);

    RoomServiceDTO update(String id, RoomServiceDTO dto);

    RoomServiceDTO getById(String id);

    List<RoomServiceDTO> getAll(String orgUnitId, RoomServiceType serviceType, Integer status);

    void delete(String id);
}

