package com.vnsky.bcss.projectbase.infrastructure.data.response.external;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckStatusMbfResponse extends BaseMbfResponse<List<CheckStatusMbfResponse.CheckStatusItem>> {

	@Data
	public static class CheckStatusItem {
		private String isdn;
		private String serial;
		private String validDatetime;
		private String activeDatetime;
		private String status;
		private String promCode;
		private String pckCode;
		private String hlrActStatus;
	}
}
