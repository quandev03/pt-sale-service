package com.vnsky.bcss.projectbase.infrastructure.data.request.hook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterSimRequest {
    private String msgId;
    private String imsi;
    private String isdn;
    private String profile;
    private String createTime;
} 