package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookESimMbfResponse extends BaseMbfResponse<List<BookESimMbfResponse.BookEsimResponseItem>> {

    @Data
    public static class BookEsimResponseItem {
        private String status;
        private BookEsimInfo data;
    }

    @Data
    public static class BookEsimInfo {
        private String format;
        private String qr;
        @JsonProperty("esim_gw_id")
        private String esimGwId;
        private String serial;
        private String imsi;
    }
}
