package com.vnsky.bcss.projectbase.infrastructure.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckOrgParentResponse  {
    private int result;
    private String orgName;
}
