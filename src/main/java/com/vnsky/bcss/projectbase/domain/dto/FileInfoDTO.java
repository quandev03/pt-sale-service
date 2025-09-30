package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FileInfoDTO {

    @Schema(description = "Tên file")
    private String fileName;

    @Schema(description = "Link lưu trữ file trên hệ thống")
    private String fileUrl;

}
