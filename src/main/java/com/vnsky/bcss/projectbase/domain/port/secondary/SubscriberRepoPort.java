package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.ExportQrEsimExcel;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface SubscriberRepoPort {

    SubscriberDTO saveAndFlush(SubscriberDTO subscriberDTO);

    Optional<SubscriberDTO> findByLastIsdn(Long isdn);

    Optional<SubscriberDTO> findByImsi(Long imsi);

    List<SubscriberDTO> saveAllAndFlush(List<SubscriberDTO> collect);

    Page<EsimInforDTO> getListEsimInfor(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, List<String> orgIdSearch, String fromDate, String toDate,Pageable pageable);

    Page<EsimInforDTO> getListEsimInforInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate, Pageable pageable);

    Optional<SubscriberDTO> findByLastSerial(String serial);
    Page<SearchSubscriberResponse> searchSubscriber(String q, Integer status, String orgCode, Pageable page);

    List<SearchSubscriberResponse> getSubscriber(String q, Integer status, String orgCode);

    Optional<SubscriberDTO> findById(String subId);

    boolean isExistByContractCodeOrCustomerCode(String contractCode, String customerCode);

    Page<SubscriberReportResponse> searchSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable);

    List<SubscriberReportResponse> getSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request);

    ESimDetailResponse findEsimDetailById(String subId);

    int isEsimBelongToAgent(Long isdn, String agentId);

    List<EsimInforDTO> getListEsimInforExport(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, List<String> orgIdSearch, String fromDate, String toDate);

    List<EsimInforDTO> getListEsimInforExportInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate);

    void delete(String id);
    List<ExportQrEsimExcel> getListEsimQrCode(List<String> subIds);

    List<SubscriberDTO> findByIds(List<String> subIds);

}
