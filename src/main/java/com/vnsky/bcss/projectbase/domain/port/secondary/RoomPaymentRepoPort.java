package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDTO;
import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDetailDTO;

import java.util.List;
import java.util.Optional;

public interface RoomPaymentRepoPort {

    RoomPaymentDTO save(RoomPaymentDTO dto);

    RoomPaymentDTO update(RoomPaymentDTO dto);

    Optional<RoomPaymentDTO> findById(String id);

    Optional<RoomPaymentDTO> findByOrgUnitIdAndMonthAndYear(String orgUnitId, Integer month, Integer year);

    List<RoomPaymentDTO> findByOrgUnitId(String orgUnitId);

    List<RoomPaymentDTO> findByFilters(String orgUnitId, Integer year, Integer month);

    List<RoomPaymentDTO> findByClientId(String clientId, Integer year, Integer month);

    RoomPaymentDetailDTO saveDetail(RoomPaymentDetailDTO dto);

    List<RoomPaymentDetailDTO> findDetailsByRoomPaymentId(String roomPaymentId);

    void deleteDetailsByRoomPaymentId(String roomPaymentId);
}

