package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.pdf.FillDataPdf;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AgreeDecree13ContractDataDto {
    @FillDataPdf(keyFill = "customer_no", isMultiFieldSameName = true)
    @Schema(description = "Mã khách hàng", example = "123456")
    private String customerId;

    @FillDataPdf(keyFill = "date_of_birth", isMultiFieldSameName = true)
    @Schema(description = "Ngày sinh", example = "01/01/1990")
    private String birthDate;

    @FillDataPdf(keyFill = "sex")
    @Schema(description = "Giới tính", example = "Nam")
    private String gender;

    @FillDataPdf(keyFill = "sex_en")
    @Schema(description = "Giới tính", example = "Nam")
    private String genderEn;

    @FillDataPdf(keyFill = "id_no", isMultiFieldSameName = true)
    @Schema(description = "Số CMND / CCCD", example = "123456789")
    private String idNo;

    @FillDataPdf(keyFill = "place_of_issue", isMultiFieldSameName = true)
    @Schema(description = "Nơi cấp", example = "Hà Nội")
    private String idPlace;

    @FillDataPdf(keyFill = "date_of_issue", isMultiFieldSameName = true)
    @Schema(description = "Ngày cấp", example = "01/01/2010")
    private String idDate;

    @FillDataPdf(keyFill = "country", isMultiFieldSameName = true)
    @Schema(description = "Quốc tịch", example = "Việt Nam")
    private String country;

    @FillDataPdf(keyFill = "contract_no", isMultiFieldSameName = true)
    @Schema(description = "Số hợp đồng", example = "123456")
    private String contractNo;

    @FillDataPdf(keyFill = "contract_date", isMultiFieldSameName = true)
    @Schema(description = "Ngày ký hợp đồng")
    private String contractDate;

    @FillDataPdf(keyFill = "agree_dk1")
    private boolean agreeDk1;

    @FillDataPdf(keyFill = "agree_dk2")
    private boolean agreeDk2;

    @FillDataPdf(keyFill = "agree_dk3")
    private boolean agreeDk3;

    @FillDataPdf(keyFill = "agree_dk4")
    private boolean agreeDk4;

    @FillDataPdf(keyFill = "agree_dk5")
    private boolean agreeDk5;

    @FillDataPdf(keyFill = "agree_dk6")
    private boolean agreeDk6;

    @FillDataPdf(keyFill = "customer_name", isMultiFieldSameName = true)
    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
    private String customerName;

    @FillDataPdf(keyFill = "represent_supply_name")
    private String representSupplyName;

    @FillDataPdf(keyFill = "employee_name")
    private String employeeName;

    @FillDataPdf(keyFill = "customer_signature", image = true, maxWidth = 1800)
    private byte[] signatureCustomer;

    @FillDataPdf(keyFill = "represent_supply_signature", image = true, maxWidth = 1800)
    private byte[] representSupplySignature;

    @FillDataPdf(keyFill = "employee_signature", image = true, maxWidth = 1800)
    private byte[] signatureCskh;
}

