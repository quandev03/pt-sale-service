package com.vnsky.bcss.projectbase.infrastructure.data.request.partner;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckOrgParentRequest {
    @NotBlank(message = "orgId không được để trống")
    private String orgId;
    
    private String clientId;
    
    @NotBlank(message = "currentUserId không được để trống")
    private String currentUserId;
}
