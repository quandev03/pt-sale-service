package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@Accessors(chain = true)
public class OrganizationUnitDTO extends CommonDTO {

    @Schema(description = "Id đơn vị")
    private String id;

    @Schema(description = "Mã đơn vị")
    @NotBlank(message = "Mã đơn vị không được bỏ trống")
    @Pattern(regexp = "[A-Za-z0-9_-]*", message = "Mã đơn vị chưa đúng định dạng")
    private String orgCode;

    @Schema(description = "Tên đơn vị")
    @NotBlank(message = "Tên đơn vị không được bỏ trống")
    private String orgName;

    @Schema(description = "Mã nhân viên")
    private String employeeCode;

    private String parentId;

    @Schema(description = "Loại đơn vị (NBO: Nội bộ)")
    private String orgType;

    @Schema(description = "Loại đơn vị con (00: trụ sở chính; 01: phòng ban; 02: trung tâm;03 chi nhánh; 04: cửa hàng)")
    private String orgSubType;

    @Schema(description = "Thông tin chi tiết đơn vị")
    private String orgDescription;

    @Schema(description = "Mã tỉnh")
    private String provinceCode;

    @Schema(description = "Mã xã")
    private String wardCode;

    @Schema(description = "Địa chỉ")
    private String address;

    @Schema(description = "Trạng thái - 1: Hoạt đông ; 0 - Không hoạt động")
    private Integer status;

    @Schema(description = "Mã số thuế")
    @Pattern(regexp = "\\d*", message = "Mã số thuế chưa đúng định dạng")
    private String taxCode;

    @Schema(description = "Mã hợp đồng")
    private String contractNo;

    @Schema(description = "Ngày hợp đồng")
    private LocalDate contractDate;

    @Schema(description = "Người đại diện")
    private String representative;

    @Schema(description = "Số điện thoại")
    private String phone;

    @Schema(description = "email")
    private String email;

    @Schema(description = "Kiểu đối tác")
    private Integer orgPartnerType;

    @Schema(description = "số tk ngân hàng")
    private String orgBankAccountNo;

    @Schema(description = "File upload hợp đồng")
    private String contractNoFileUrl;

    @Schema(description = "File upload GPDKKD")
    private String businessLicenseFileUrl;

    @Schema(description = "Số giấy phép DKKD")
    private String businessLicenseNo;

    @Schema(description = "Địa chỉ giấy phép DKKD")
    private String businessLicenseAddress;

    @Schema(description = "thông tin vận chuyển")
    private List<OrganizationDeliveryInfoDTO> deliveryInfos = new ArrayList<>();

    @Schema(description = "link file hợp đồng")
    private String contractNoFileLink;

    @Schema(description = "link file đăng kí kinh doanh")
    private String businessLicenseFileLink;

    @Schema(description = "trạng thái phê duyệt")
    private Integer approvalStatus;

    @Schema(description = "id client")
    private String clientId;

    @Schema(description = "Kênh bán")
    private String saleChanel;

    @Schema(description = "Phân vùng giao hàng")
    private String deliveryAreas;

    @Schema(description = "Mã đại lý cha")
    private String parentCode;

    @Schema(description = "Công nợ tạm tính")
    private Long debtLimit;

    @Schema(description = "Công nợ thực tế")
    private Long debtLimitMbf;

    @Schema(description = "CCCD")
    private String cccd;

    @Schema(description = "Danh sách đường dẫn ảnh")
    private List<String> imageUrls = new ArrayList<>();
}
