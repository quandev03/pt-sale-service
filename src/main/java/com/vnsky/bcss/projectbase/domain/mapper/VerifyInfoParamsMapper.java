package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.ApplicationConfigDTO;
import com.vnsky.bcss.projectbase.domain.dto.VerifyInfoParams;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.util.stream.Collectors;

@Slf4j
@Component
public class VerifyInfoParamsMapper {

    private static final String VERIFY_INFO_SUBSCRIBER_TYPE = "VERIFY_INFO_SUBSCRIBER";

    private static final String ARR_IMAGES_IMAGE_TYPE = "0";      // CCCD images

    private final MinioOperations minioOperations;

    public VerifyInfoParamsMapper(MinioOperations minioOperations) {
        this.minioOperations = minioOperations;
    }

    public VerifyInfoParams mapToVerifyInfoParams(List<ApplicationConfigDTO> configs) {
        if (configs == null || configs.isEmpty()) {
            log.warn("No configurations provided for mapping to VerifyInfoParams");
            return createDefaultVerifyInfoParams();
        }

        // Convert list to map for easier access
        Map<String, String> configMap = configs.stream()
            .filter(config -> VERIFY_INFO_SUBSCRIBER_TYPE.equals(config.getType()))
            .collect(Collectors.toMap(
                ApplicationConfigDTO::getCode,
                config -> config.getName() != null ? config.getName() : ""
            ));

        return VerifyInfoParams.builder()
            .strSex(getConfigValue(configMap, "strSex", "0"))
            .strNationality(getConfigValue(configMap, "strNationality", "VNM"))
            .strSubName(getConfigValue(configMap, "strSubName", ""))
            .strBirthday(getConfigValue(configMap, "strBirthday", ""))
            .strProvince(getConfigValue(configMap, "strProvince", ""))
            .strDistrict(getConfigValue(configMap, "strDistrict", ""))
            .strPrecinct(getConfigValue(configMap, "strPrecinct", ""))
            .strSubType(getConfigValue(configMap, "strSubType", "HVN"))
            .strCustType(getConfigValue(configMap, "strCustType", "HVN"))
            .strReasonCode(getConfigValue(configMap, "strReasonCode", "KHYC"))
            .strAppObject(getConfigValue(configMap, "strAppObject", "CDN1"))

            .strIdNo(getConfigValue(configMap, "strIdNo", ""))
            .strIdIssueDate(getConfigValue(configMap, "strIdIssueDate", ""))
            .strIdIssuePlace(getConfigValue(configMap, "strIdIssuePlace", ""))

            .strHome(getConfigValue(configMap, "strHome", ""))
            .strAddress(getConfigValue(configMap, "strAddress", ""))
            .strRegType(getConfigValue(configMap, "strRegType", "MS"))
            .strKitType(getConfigValue(configMap, "strKitType", "1"))
            .strContractNo(getConfigValue(configMap, "strContractNo", ""))
            .strSignDate(getConfigValue(configMap, "strSignDate", ""))
            .strMobiType(getConfigValue(configMap, "strMobiType", "MC"))

            .strUserSubName(getConfigValue(configMap, "strUserSubName", ""))
            .strUserBirthday(getConfigValue(configMap, "strUserBirthday", ""))
            .strUserSex(getConfigValue(configMap, "strUserSex", "0"))
            .strUserOption(getConfigValue(configMap, "strUserOption", "0"))
            .strUserIdOrPpNo(getConfigValue(configMap, "strUserIdOrPpNo", ""))
            .strUserIdOrPpIssueDate(getConfigValue(configMap, "strUserIdOrPpIssueDate", ""))
            .strUserIdOrPpIssuePlace(getConfigValue(configMap, "strUserIdOrPpIssuePlace", ""))
            .strUserProvince(getConfigValue(configMap, "strUserProvince", ""))
            .strUserDistrict(getConfigValue(configMap, "strUserDistrict", ""))
            .strUserPrecinct(getConfigValue(configMap, "strUserPrecinct", ""))
            .strUserStreetBlockName(getConfigValue(configMap, "strUserStreetBlockName", null))
            .strUserStreetName(getConfigValue(configMap, "strUserStreetName", null))
            .strUserHome(getConfigValue(configMap, "strUserHome", null))
            .strUserNationality(getConfigValue(configMap, "strUserNationality", ""))

            .strRegBussiness(getConfigValue(configMap, "strRegBussiness", ""))
            .strFoundedPermNo(getConfigValue(configMap, "strFoundedPermNo", ""))
            .strContactAddress(getConfigValue(configMap, "strContactAddress", ""))
            .strBusPermitNo(getConfigValue(configMap, "strBusPermitNo", ""))
            .strContactName(getConfigValue(configMap, "strContactName", ""))
            .strFoundedPermDate(getConfigValue(configMap, "strFoundedPermDate", ""))
            .strTin(getConfigValue(configMap, "strTin", ""))
            .strTel(getConfigValue(configMap, "strTel", ""))
            .strOption(getConfigValue(configMap, "strOption", "0"))

            .arrImages(parseImagesConfig(getConfigValue(configMap, "arrImages", "")))
            .build();
    }

    private VerifyInfoParams createDefaultVerifyInfoParams() {
        return VerifyInfoParams.builder()
            .strSex("0")
            .strNationality("VNM")
            .strSubName("")
            .strBirthday("")
            .strProvince("")
            .strDistrict("")
            .strPrecinct("")
            .strSubType("HVN")
            .strCustType("HVN")
            .strReasonCode("KHYC")
            .strAppObject("CDN1")
            .strIdNo("")
            .strIdIssueDate("")
            .strIdIssuePlace("")
            .strHome("")
            .strAddress("")
            .strRegType("MS")
            .strKitType("1")
            .strContractNo("")
            .strSignDate("")
            .strMobiType("MC")
            .strUserSubName("")
            .strUserBirthday("")
            .strUserSex("0")
            .strUserOption("0")
            .strUserIdOrPpNo("")
            .strUserIdOrPpIssueDate("")
            .strUserIdOrPpIssuePlace("")
            .strUserProvince("")
            .strUserDistrict("")
            .strUserPrecinct("")
            .strUserStreetBlockName(null)
            .strUserStreetName(null)
            .strUserHome(null)
            .strUserNationality("")
            .strRegBussiness("")
            .strFoundedPermNo("")
            .strContactAddress("")
            .strBusPermitNo("")
            .strContactName("")
            .strFoundedPermDate("")
            .strTin("")
            .strTel("")
            .strOption("0")
            .arrImages(new ArrayList<>())
            .build();
    }

    private String getConfigValue(Map<String, String> configMap, String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    private List<List<String>> parseImagesConfig(String imagesConfig) {
        if (imagesConfig == null || imagesConfig.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<List<String>> images = new ArrayList<>();
        String[] imageEntries = imagesConfig.split(";");

        for (String entry : imageEntries) {
            String[] parts = entry.split(",");
            List<String> imageData = new ArrayList<>();

            String filename = parts.length > 0 ? parts[0] : "";
            imageData.add(filename);

            String base64Image = "";
            if (parts.length > 1 && parts[1] != null && !parts[1].trim().isEmpty()) {
                try {
                    base64Image = buildBase64ImageFromMinio(parts[1]);
                } catch (Exception e) {
                    log.warn("Failed to download and convert image from MinIO URL: {}, error: {}", parts[1], e.getMessage());
                    base64Image = "";
                }
            }
            imageData.add(base64Image);

            // ARR_IMAGES_IMAGE_TYPE (0): CCCD Front, CCCD Back
            // ARR_IMAGES_PORTRAIT_TYPE (1): Portrait images
            // ARR_IMAGES_CONTRACT_TYPE (2): Contract documents
            String imageType = parts.length > 2 ? parts[2] : ARR_IMAGES_IMAGE_TYPE;
            imageData.add(imageType);

            images.add(imageData);
        }

        return images;
    }

    private String buildBase64ImageFromMinio(String minioUrl) throws IOException {
        if (minioUrl == null || minioUrl.trim().isEmpty()) {
            return "";
        }

        try {
            // Download file from MinIO
            DownloadOptionDTO downloadOption = DownloadOptionDTO.builder()
                .uri(minioUrl)
                .isPublic(false)
                .build();

            Resource fileResource = minioOperations.download(downloadOption);

            // Convert to base64
            byte[] fileContent = fileResource.getContentAsByteArray();
            return Base64.getEncoder().encodeToString(fileContent);

        } catch (Exception e) {
            log.error("Failed to download and convert file from MinIO URL: {}, error: {}", minioUrl, e.getMessage());
            throw new IOException("Failed to process file from MinIO: " + minioUrl, e);
        }
    }
}
