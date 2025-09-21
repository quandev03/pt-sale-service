package com.vnsky.bcss.projectbase.infrastructure.data.request.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CraftKitMbfRequest {
    private String isdn;
    private String serial;
    private String bhm;
    private String user;
    private boolean getSuccess;
} 