package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.StockIsdnStatus;
import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchSubscriberResponse {

    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("ISDN")
    private Integer isdn;

    @DbColumnMapper("IMSI")
    private Integer imsi;

    @DbColumnMapper("SERIAL")
    private String serial;

    @DbColumnMapper("VERIFIED_STATUS")
    private Integer verifiedStatus;

    @DbColumnMapper("ACTIVE_STATUS")
    private Integer activeStatus;

    @DbColumnMapper("STATUS")
    private Integer status;

    @DbColumnMapper("STATUS_900")
    private Integer statusCall900;

    @DbColumnMapper("BOUGHT_STATUS")
    private Integer boughtStatus;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    // Property ảo, vẫn xuất hiện trong JSON
    @JsonProperty("statusText")
    public String getStatusText() {
        return StockIsdnStatus.fromResponse(this).getDescription();
    }
}
