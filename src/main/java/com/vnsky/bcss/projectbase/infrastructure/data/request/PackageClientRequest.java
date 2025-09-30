package com.vnsky.bcss.projectbase.infrastructure.data.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageClientRequest {
    @NotNull(message = "ClientID không được để null")
    private String clientId;

    private List<String> packageCodes;
}
