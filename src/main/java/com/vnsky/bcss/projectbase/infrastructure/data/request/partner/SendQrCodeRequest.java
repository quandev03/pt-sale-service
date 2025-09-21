package com.vnsky.bcss.projectbase.infrastructure.data.request.partner;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendQrCodeRequest {
    private String subId;
    private String email;
}
