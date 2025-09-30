package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MbfPartnerInfoResponse extends BaseMbfResponse<List<MbfPartnerInfoResponse.MbfPartnerInfo>> {
    @Data
    public static class MbfPartnerInfo {
        private String shopId;
        private String shopCode;
        private String shopName;
        private String shopType;
        private String provinceCode;
        private String billingShopCode;
        private String parentShopId;
        private String parentShopCode;
        private String shopStatus;
        private String centerCode;
        private String shopAddress;
        private String telNumber;
        private String taxCode;
        private String sunShopCode;
        private String companyName;
        private String contactName;
        private String cashier;
        private String areaCode;
    }
}
