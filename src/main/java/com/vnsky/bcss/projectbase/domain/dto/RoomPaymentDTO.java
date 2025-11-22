package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoomPaymentDTO extends AbstractAuditDTO {
    private String id;
    private String orgUnitId;
    private Integer month;
    private Integer year;
    private BigDecimal totalAmount;
    private String qrCodeUrl;
    private byte[] qrCodeImage;
    private Integer status;
    private LocalDateTime paymentDate;
    private List<RoomPaymentDetailDTO> details = new ArrayList<>();
}

