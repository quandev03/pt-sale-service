package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.RoomServiceDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.RoomServiceServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateRoomServiceRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.UpdateRoomServiceRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.RoomServiceResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.RoomServiceOperation;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RoomServiceRest implements RoomServiceOperation {

    private final RoomServiceServicePort roomServiceServicePort;

    @Override
    public ResponseEntity<RoomServiceResponse> createRoomService(CreateRoomServiceRequest request) {
        RoomServiceDTO dto = mapToDTO(request);
        RoomServiceDTO created = roomServiceServicePort.create(dto);
        return ResponseEntity.ok(mapToResponse(created));
    }

    @Override
    public ResponseEntity<RoomServiceResponse> updateRoomService(String id, UpdateRoomServiceRequest request) {
        RoomServiceDTO dto = mapToDTO(request);
        RoomServiceDTO updated = roomServiceServicePort.update(id, dto);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @Override
    public ResponseEntity<Object> deleteRoomService(String id) {
        roomServiceServicePort.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<RoomServiceResponse>> getAllRoomServices(
        @RequestParam(required = false) String orgUnitId,
        @RequestParam(required = false) RoomServiceType serviceType,
        @RequestParam(required = false) Integer status) {
        List<RoomServiceDTO> dtos = roomServiceServicePort.getAll(orgUnitId, serviceType, status);
        List<RoomServiceResponse> responses = dtos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<RoomServiceResponse> getRoomServiceById(String id) {
        RoomServiceDTO dto = roomServiceServicePort.getById(id);
        return ResponseEntity.ok(mapToResponse(dto));
    }

    private RoomServiceDTO mapToDTO(CreateRoomServiceRequest request) {
        return RoomServiceDTO.builder()
            .orgUnitId(request.getOrgUnitId())
            .serviceType(request.getServiceType())
            .serviceCode(request.getServiceCode())
            .serviceName(request.getServiceName())
            .price(request.getPrice())
            .status(request.getStatus())
            .build();
    }

    private RoomServiceDTO mapToDTO(UpdateRoomServiceRequest request) {
        return RoomServiceDTO.builder()
            .orgUnitId(request.getOrgUnitId())
            .serviceType(request.getServiceType())
            .serviceCode(request.getServiceCode())
            .serviceName(request.getServiceName())
            .price(request.getPrice())
            .status(request.getStatus())
            .build();
    }

    private RoomServiceResponse mapToResponse(RoomServiceDTO dto) {
        return RoomServiceResponse.builder()
            .id(dto.getId())
            .orgUnitId(dto.getOrgUnitId())
            .serviceType(dto.getServiceType())
            .serviceCode(dto.getServiceCode())
            .serviceName(dto.getServiceName())
            .price(dto.getPrice())
            .clientId(dto.getClientId())
            .status(dto.getStatus())
            .createdBy(dto.getCreatedBy())
            .createdDate(dto.getCreatedDate())
            .modifiedBy(dto.getModifiedBy())
            .modifiedDate(dto.getModifiedDate())
            .build();
    }
}

