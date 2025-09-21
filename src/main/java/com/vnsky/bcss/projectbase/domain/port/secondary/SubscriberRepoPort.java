package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepoPort {
    Optional<SubscriberDTO> findBySerialAndStatus(String serial, int status);

    SubscriberDTO saveAndFlush(SubscriberDTO subscriberDTO);

    Optional<SubscriberDTO> findByLastIsdn(Long isdn);

    Optional<SubscriberDTO> findBySerialLastSerial(String serial);

    Optional<SubscriberDTO> findByImsi(Long imsi);

    List<SubscriberDTO> saveAllAndFlush(List<SubscriberDTO> collect);

    List<SubscriberDTO> findSubscriberToBookEsim(int limit);

    Page<EsimInforDTO> getListEsimInfor(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, String orgIdSearch, Pageable pageable);

    Page<EsimInforDTO> getListEsimInforInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, Pageable pageable);

    Optional<SubscriberDTO> findById(String subId);

    ESimDetailResponse findEsimDetailById(String subId);

    int isEsimBelongToAgent(Long isdn, String agentId);

    List<EsimInforDTO> getListEsimInforExport(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, String orgIdSearch);

    List<EsimInforDTO> getListEsimInforExportInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId);

}
