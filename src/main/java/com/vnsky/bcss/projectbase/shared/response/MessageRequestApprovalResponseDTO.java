package com.vnsky.bcss.projectbase.shared.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class MessageRequestApprovalResponseDTO {

    @Schema(description = "table_name")
    private String tableName;

    @Schema(description = "record_id")
    private String recordId;

    @Schema(description = "status")
    private Integer status;

    @Schema(description = "process_code")
    private String processCode;

    @Schema(description = "approval_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalDate;

    @Schema(description = "org_id")
    private String orgId;

    @Schema(description = "description")
    private String description;
}
