package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomPaymentServicePort {

    List<RoomPaymentDTO> processExcelAndCreatePayments(MultipartFile file, Integer month, Integer year);

    RoomPaymentDTO getById(String id);

    List<RoomPaymentDTO> getAll(String orgUnitId, Integer year, Integer month);

    void resendEmail(String paymentId);
}

