package com.vnsky.bcss.projectbase.infrastructure.data.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class OrganizationUnitImageResponse {
    List<String> imageUrls;
}

