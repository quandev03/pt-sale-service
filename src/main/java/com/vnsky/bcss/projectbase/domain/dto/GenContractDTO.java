package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.pdf.FillDataPdf;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenContractDTO {

    @FillDataPdf(keyFill = "contract_no", isMultiFieldSameName = true)
    @Schema(description = "Số hợp đồng", example = "123456")
    private String contractNo;

    @FillDataPdf(keyFill = "date")
    @Schema(description = "ngày", example = "12")
    private Integer date;

    @FillDataPdf(keyFill = "month")
    @Schema(description = "tháng", example = "12")
    private Integer month;

    @FillDataPdf(keyFill = "year")
    @Schema(description = "năm", example = "2001")
    private Integer year;

    @FillDataPdf(keyFill = "customer_no", isMultiFieldSameName = true)
    @Schema(description = "Mã khách hàng", example = "123456")
    private String customerId;

    @FillDataPdf(keyFill = "ccdvvt")
    @Schema(description = "CCDVVT", example = "HN001")
    private String ccdvvt;

    @FillDataPdf(keyFill = "contract_date")
    @Schema(description = "Ngày ký hợp đồng", example = "01/01/2021 (với màn kiểm duyệt là ngày upload giấy tờ )")
    private String contractDate;

    @FillDataPdf(keyFill = "customer_name", isMultiFieldSameName = true)
    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
    private String customerName;

    @FillDataPdf(keyFill = "sex", isMultiFieldSameName = true)
    @Schema(description = "Giới tính", example = "Nam")
    private String gender;

    @FillDataPdf(keyFill = "sex_en", isMultiFieldSameName = true)
    @Schema(description = "Giới tính", example = "Nam")
    private String genderEn;

    @FillDataPdf(keyFill = "date_of_birth", isMultiFieldSameName = true)
    @Schema(description = "Ngày sinh", example = "01/01/1990")
    private String birthDate;

    @FillDataPdf(keyFill = "id_no", isMultiFieldSameName = true)
    @Schema(description = "Số CMND / CCCD", example = "123456789")
    private String idNo;

    @FillDataPdf(keyFill = "date_of_issue", isMultiFieldSameName = true)
    @Schema(description = "Ngày cấp", example = "01/01/2010")
    private String idDate;

    @FillDataPdf(keyFill = "place_of_issue", isMultiFieldSameName = true)
    @Schema(description = "Nơi cấp", example = "Hà Nội")
    private String idPlace;

    @FillDataPdf(keyFill = "address")
    @Schema(description = "Địa chỉ", example = "Tôn Thất Thuyết-Mỹ Đình - Nam Từ Liêm - Hà Nội")
    private String address;

    @FillDataPdf(keyFill = "country", isMultiFieldSameName = true)
    @Schema(description = "Quốc tịch", example = "Việt Nam")
    private String country;

    @FillDataPdf(keyFill = "phone_number")
    @Schema(description = "Số điện thoại", example = "0123456789")
    private String phoneNumber;

    @FillDataPdf(keyFill = "email")
    @Schema(description = "Email", example = "hehe@gmail.com")
    private String email;

    @FillDataPdf(keyFill = "isdn", fontSize = 9)
    private String isdn;

    @FillDataPdf(keyFill = "signature", image = true, maxWidth = 1800)
    private byte[] signatureCskh;

    @FillDataPdf(keyFill = "employee_name")
    private String employeeName;

    @FillDataPdf(keyFill = "signature_customer", image = true, maxWidth = 1800)
    private byte[] signatureCustomer;

    @Schema(description = "deviceToken", example = "1203741jjahskdjf9081234hkkas")
    private String deviceToken;

    @FillDataPdf(keyFill = "agree_dk1")
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk1 = true;

    @FillDataPdf(keyFill = "agree_dk2")
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk2 = true;

    @FillDataPdf(keyFill = "agree_dk3")
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk3 = true;

    @FillDataPdf(keyFill = "agree_dk4")
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk4 = true;

    @FillDataPdf(keyFill = "agree_dk5")
    @NotNull(message = Constant.REQUIRED_FIELD)
    private boolean agreeDk5 = true;
}
