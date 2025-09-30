package com.vnsky.bcss.projectbase.infrastructure.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriberStatusResponse {
    private String statusText;
    private Integer statusValue;
}
