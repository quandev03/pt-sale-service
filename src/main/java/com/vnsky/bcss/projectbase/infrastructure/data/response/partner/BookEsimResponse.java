package com.vnsky.bcss.projectbase.infrastructure.data.response.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEsimResponse {
    private List<String> serials;
    private List<String> qrCodes;
    private String status;
    private String message;
    private LocalDateTime createdDate;
}
