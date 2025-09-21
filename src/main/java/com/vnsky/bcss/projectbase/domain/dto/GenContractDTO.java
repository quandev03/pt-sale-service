package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.pdf.CheckboxDocx;
import com.vnsky.bcss.projectbase.shared.pdf.FillDataPdf;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenContractDTO {

    @FillDataPdf(keyFill = "contract_no")
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

    @FillDataPdf(keyFill = "customer_no")
    @Schema(description = "Mã khách hàng", example = "123456")
    private String customerId;

    @FillDataPdf(keyFill = "ccdvvt")
    @Schema(description = "CCDVVT", example = "HN001")
    private String ccdvvt;

    @FillDataPdf(keyFill = "contract_date")
    @Schema(description = "Ngày ký hợp đồng", example = "01/01/2021 (với màn kiểm duyệt là ngày upload giấy tờ )")
    private String contractDate;

    @FillDataPdf(keyFill = "customer_name")
    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
    private String customerName;

    @FillDataPdf(keyFill = "sex")
    @Schema(description = "Giới tính", example = "Nam")
    private String gender;

    @FillDataPdf(keyFill = "date_of_birth")
    @Schema(description = "Ngày sinh", example = "01/01/1990")
    private String birthDate;

    @FillDataPdf(keyFill = "id_no")
    @Schema(description = "Số CMND / CCCD", example = "123456789")
    private String idNo;

    @FillDataPdf(keyFill = "date_of_issue")
    @Schema(description = "Ngày cấp", example = "01/01/2010")
    private String idDate;

    @FillDataPdf(keyFill = "place_of_issue")
    @Schema(description = "Nơi cấp", example = "Hà Nội")
    private String idPlace;

    @FillDataPdf(keyFill = "address")
    @Schema(description = "Địa chỉ", example = "Tôn Thất Thuyết-Mỹ Đình - Nam Từ Liêm - Hà Nội")
    private String address;

    @FillDataPdf(keyFill = "visa")
    @Schema(description = "Visa", example = "123456")
    private String visaNo;

    @FillDataPdf(keyFill = "country")
    @Schema(description = "Quốc tịch", example = "Việt Nam")
    private String country;

    @FillDataPdf(keyFill = "phone_number")
    @Schema(description = "Số điện thoại", example = "0123456789")
    private String phoneNumber;


    @FillDataPdf(keyFill = "email")
    @Schema(description = "Email", example = "hehe@gmail.com")
    private String email;

    // list số thuê bao
    @Schema(description = "Danh sách số thuê bao")
    @NotNull(message = "Danh sách số thuê bao không được để trống")
    @Valid
    private List<PhoneList> phoneLists;

    @FillDataPdf(keyFill = "signature", image = true, maxWidth = 1000)
    private byte[] signatureImage;

    @FillDataPdf(keyFill = "signature-detail")
    private String signatureDetail;

    @FillDataPdf(keyFill = "signature_customer", image = true, maxWidth = 1000)
    private byte[] signatureCustomer;

    @FillDataPdf(keyFill = "signature_emp_check", image = true, maxWidth = 1000)
    private byte[] signatureEmpCheck;

    @FillDataPdf(keyFill = "signature-check")
    private String signatureCheck;

    private String type;

    @Schema(description = "deviceToken", example = "1203741jjahskdjf9081234hkkas")
    private String deviceToken;

    @Data
    public static class PhoneList {

        @FillDataPdf(keyFill = "p_number")
        @Schema(description = "Số thuê bao", example = "0123456789")
        private String phoneNumber;

        @FillDataPdf(keyFill = "serial")
        @Schema(description = "Serial", example = "123456")
        private String serialSim;

        @FillDataPdf(keyFill = "p")
        @Schema(description = "Gói cước", example = "Gói cước 1")
        private String packagePlan;

        @FillDataPdf(keyFill = "o")
        @Schema(description = "Đối tượng", example = "Đối tác 1")
        private String object;

        @FillDataPdf(keyFill = "note")
        @Schema(description = "Ghi chú", example = "Ghi chú 1")
        private String note;
    }

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
