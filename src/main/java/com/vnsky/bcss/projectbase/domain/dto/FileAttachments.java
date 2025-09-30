package com.vnsky.bcss.projectbase.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class FileAttachments {
    private MultipartFile contractFile;
    private MultipartFile businessLicenseFile;
    private MultipartFile idCardFrontSite;
    private MultipartFile idCardBackSite;
    private MultipartFile portrait;
}
