package com.vnsky.bcss.projectbase.infrastructure.data.request.partner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportQrCodeRequest {
    private List<String> subIds;
}
