package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.pdf.CheckboxDocx;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgreeDegree13DTO {
    @CheckboxDocx(index = 0)
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk1;

    @CheckboxDocx(index = 1)
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk2;

    @CheckboxDocx(index = 2)
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk3;

    @CheckboxDocx(index = 3)
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk4;

    @CheckboxDocx(index = 4)
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk5;

    @CheckboxDocx(index = 5)
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk6;
}
