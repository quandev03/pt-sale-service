package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.ParamDTO;

import java.util.List;
import java.util.Map;

public interface AppPickListServicePort {

    Map<String, List<ParamDTO>> getAppPickList();
}
