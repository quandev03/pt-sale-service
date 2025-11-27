package com.vnsky.bcss.projectbase.infrastructure.data.request.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vnsky.bcss.projectbase.shared.utils.IsoLocalDateTimeDeserializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request mua gói dịch vụ cho web partner.
 * organizationUnitId sẽ được lấy từ tổ chức hiện tại, không cho phép client truyền lên.
 */
@Data
public class PartnerPackageSubscriptionBuyRequest {

    /**
     * ID cấu hình gói cước muốn mua.
     */
    @NotBlank
    private String packageProfileId;

    /**
     * Thời điểm bắt đầu hiệu lực gói.
     * Nếu không truyền, backend sẽ dùng thời điểm hiện tại.
     */
    @JsonDeserialize(using = IsoLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;
}



