package com.vnsky.bcss.projectbase.infrastructure.data.request.partner;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EsimBookingRequest {
    
    @NotNull(message = "Danh sách yêu cầu không được để trống")
    @NotEmpty(message = "Danh sách yêu cầu không được để trống")
    @Valid
    private List<BookEsimRequest> requests;
    
    private String note;
}
