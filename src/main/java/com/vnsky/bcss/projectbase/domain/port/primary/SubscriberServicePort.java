package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.PackageReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberStatusResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

public interface SubscriberServicePort {
    SubscriberDTO saveAndFlushNewTransaction(SubscriberDTO dto);

    SubscriberDTO findByIsdn(Long isdn);

    Resource downloadFile(String url);

    Page<SearchSubscriberResponse> searchSubscriber(String q, Integer status, String orgCode, Pageable page);

    Page<SubscriberReportResponse> searchSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable);

    ByteArrayOutputStream exportSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request);

    ByteArrayOutputStream exportSubscriber(String q, Integer status, String orgCode);

    List<SubscriberStatusResponse> getAllStatus();
}
