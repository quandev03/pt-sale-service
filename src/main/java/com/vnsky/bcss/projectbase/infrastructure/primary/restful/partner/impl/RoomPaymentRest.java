package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDTO;
import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDetailDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.RoomPaymentServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.RoomPaymentDetailResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.RoomPaymentResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.RoomPaymentOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RoomPaymentRest implements RoomPaymentOperation {

    private final RoomPaymentServicePort roomPaymentServicePort;

    @Override
    public ResponseEntity<List<RoomPaymentResponse>> uploadAndProcess(
        @RequestPart("file") MultipartFile file,
        @RequestPart(value = "month", required = true) String month,
        @RequestPart(value = "year", required = true) String year) {
        // Convert String to Integer
        try {
            Integer monthInt = Integer.parseInt(month);
            Integer yearInt = Integer.parseInt(year);
            
            List<RoomPaymentDTO> payments = roomPaymentServicePort.processExcelAndCreatePayments(file, monthInt, yearInt);
            List<RoomPaymentResponse> responses = payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Tháng và năm phải là số hợp lệ: month=" + month + ", year=" + year);
        }
    }

    @Override
    public ResponseEntity<List<RoomPaymentResponse>> getAll(
        @RequestParam(required = false) String orgUnitId,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month) {
        List<RoomPaymentDTO> payments = roomPaymentServicePort.getAll(orgUnitId, year, month);
        List<RoomPaymentResponse> responses = payments.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<RoomPaymentResponse> getById(String id) {
        RoomPaymentDTO payment = roomPaymentServicePort.getById(id);
        return ResponseEntity.ok(mapToResponse(payment));
    }

    private RoomPaymentResponse mapToResponse(RoomPaymentDTO dto) {
        List<RoomPaymentDetailResponse> detailResponses = dto.getDetails() != null ?
            dto.getDetails().stream()
                .map(this::mapDetailToResponse)
                .collect(Collectors.toList()) : List.of();

        return RoomPaymentResponse.builder()
            .id(dto.getId())
            .orgUnitId(dto.getOrgUnitId())
            .month(dto.getMonth())
            .year(dto.getYear())
            .totalAmount(dto.getTotalAmount())
            .qrCodeUrl(dto.getQrCodeUrl()) // QR code URL để hiển thị
            .status(dto.getStatus())
            .paymentDate(dto.getPaymentDate())
            .details(detailResponses)
            .createdBy(dto.getCreatedBy())
            .createdDate(dto.getCreatedDate())
            .build();
    }

    private RoomPaymentDetailResponse mapDetailToResponse(RoomPaymentDetailDTO detail) {
        return RoomPaymentDetailResponse.builder()
            .id(detail.getId())
            .serviceType(detail.getServiceType())
            .serviceName(detail.getServiceName())
            .quantity(detail.getQuantity())
            .unitPrice(detail.getUnitPrice())
            .amount(detail.getAmount())
            .build();
    }
}

