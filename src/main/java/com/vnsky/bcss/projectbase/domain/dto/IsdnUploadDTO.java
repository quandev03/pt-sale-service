package com.vnsky.bcss.projectbase.domain.dto;


import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.excel.annotation.CsvColumn;
import com.vnsky.excel.annotation.XlsxColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IsdnUploadDTO implements NumberUploadDTO {

    @Schema(description = "Số")
    @XlsxColumn(writeIndex = 0, header = "Số thuê bao", readIndex = 0)
    @CsvColumn(writeIndex = 0, header = "So thue bao", readIndex = 0)
    private String isdn;

    @Schema(description = "Mô tả")
    @XlsxColumn(writeIndex = 1, header = "Ghi chú", readIndex = 1)
    @CsvColumn(writeIndex = 1, header = "Ghi chu", readIndex = 1)
    private String description;

    @XlsxColumn(writeIndex = 2, header = "Kết quả", ignore = true)
    @CsvColumn(writeIndex = 2, header = "Ket qua", ignore = true)
    private String result;

    @XlsxColumn(writeIndex = 3, header = "Lý do", ignore = true)
    @CsvColumn(writeIndex = 3, header = "Ly do", ignore = true)
    private String reason;

    private Long isdnTruncated = null;

    private final Set<String> errors = new HashSet<>();

    @Override
    public long getIsdnTruncated() {
        if (isdnTruncated == null) {
            this.isdnTruncated = doIsdnTruncated();
        }
        return this.isdnTruncated;
    }

    @Override
    public Set<String> getErrors() {
        return this.errors;
    }

    @Override
    public void appendError(String error) {
        this.errors.add(error);
    }

    @Override
    public void appendError(Set<String> errors) {
        this.errors.addAll(errors);
    }

    @Override
    public void finalizeResult() {
        boolean hasErrors = !getErrors().isEmpty();
        this.setResult(hasErrors ? Constant.MESSAGE_FAILURE : Constant.MESSAGE_SUCCESS);
    }

}
