package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OrganizationDeliveryInfoDTO extends CommonDTO{
    @Schema(description = "Id")
    private String id;

    @Schema(description = "ID đối tác")
    private String orgId;

    @Schema(description = "mã tỉnh/thành phố")
    private String provinceCode;

    @Schema(description = "mã quận/huyện")
    private String districtCode;

    @Schema(description = "mã phường/xã")
    private String wardCode;

    @Schema(description = "Địa chỉ nhận hàng")
    private String address;

    @Schema(description = "Tên người đại diện")
    private String consigneeName;

    @Schema(description = "Địa chỉ người đại diện")
    private String consigneeAddress;

    @Schema(description = "chức danh")
    private String orgTitle;

    @Schema(description = "Số CCCD")
    private String idNo;

    @Schema(description = "ngày cấp cccd")
    private String idDate;

    @Schema(description = "nơi cấp CCCD")
    private String idPlace;

    @Schema(description = "ảnh CCCD mặt trước")
    private String idCardFrontSiteFileUrl;

    @Schema(description = "ảnh CCCD mặt sau")
    private String idCardBackSiteFileUrl;

    @Schema(description = "ảnh chân dung")
    private String multiFileUrl;

    @Schema(description = "Giới tính")
    private String gender;

    @Schema(description = "ngày sinh")
    private LocalDate dateOfBirth;

    @Schema(description = "địa chỉ email")
    private String email;

    @Schema(description = "số điẹn thoại")
    private String phone;

    @Schema(description = "hộ chiếu")
    private String passportNo;

    @Schema(description = "trạng thái")
    private Boolean status;

    @Schema(description = "link ảnh mặt trước cccd")
    private String idCardFrontSiteFileLink;

    @Schema(description = "link ảnh mặt sau cccd")
    private String idCardBackSiteFileLink;

    @Schema(description = "link ảnh chân dung")
    private String multiFileLink;
}
