package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CraftKitMbfResponse extends BaseMbfResponse<List<CraftKitMbfResponse.DataItem>> {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataItem {
        private String code;
        private String message;
        private Object data;
    }
}