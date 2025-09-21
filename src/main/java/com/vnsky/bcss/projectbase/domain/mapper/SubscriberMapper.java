package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.ActiveSubscriberDataDTO;
import com.vnsky.bcss.projectbase.domain.dto.ModifyInforParamsDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.UpdateSubscriberDataMbfRequest;
import com.vnsky.bcss.projectbase.domain.entity.SubscriberEntity;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Gender;
import org.mapstruct.Mapper;
import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriberMapper extends BaseMapper<SubscriberEntity, SubscriberDTO> {
    default void mapFromSubmitData(SubscriberDTO esimRegistration, ActiveSubscriberDataDTO activeSubscriberData){
        if(activeSubscriberData.getOcrData().getDob() != null){
            esimRegistration.setDateOfBirth(LocalDate.parse(activeSubscriberData.getOcrData().getDob(), Constant.FE_DATE_FORMATTER));
        }

        if(activeSubscriberData.getOcrData().getExpiredDate() != null){
            esimRegistration.setIdNoExpiredDate(LocalDate.parse(activeSubscriberData.getOcrData().getExpiredDate(), Constant.FE_DATE_FORMATTER));
        }

        esimRegistration.setGender(Gender.fromValue(activeSubscriberData.getOcrData().getGender()).getCode());
        esimRegistration.setIdNumber(activeSubscriberData.getOcrData().getIdNumber());
        esimRegistration.setIdNoIssuedPlace(activeSubscriberData.getOcrData().getIssuedPlace());
        esimRegistration.setNicNumber(activeSubscriberData.getOcrData().getNicNumber());
        esimRegistration.setPlaceOfBirth(activeSubscriberData.getOcrData().getPlaceOfBirth());
        esimRegistration.setFullName(activeSubscriberData.getOcrData().getFullname());
        esimRegistration.setNationality(activeSubscriberData.getOcrData().getNationality());
    }

    default UpdateSubscriberDataMbfRequest mapUpdateMbfData(SubscriberDTO esimRegistration, List<List<String>> buildArrImagesForUpdateMbf, ModifyInforParamsDTO params) {
        return UpdateSubscriberDataMbfRequest.builder()
            .strIsdn(esimRegistration.getIsdn())
            .strSerial(esimRegistration.getSerial())
            .strSubType(params.getStrSubType())
            .strCustType(params.getStrCustType())
            .strReasonCode(params.getStrReasonCode())
            .strActionFlag(params.getStrActionFlag())
            .strAppObject(params.getStrAppObject())
            .strPasspost(esimRegistration.getIdNumber())
            .strPasspostIssuePlace(esimRegistration.getIdNoIssuedPlace())
            .strPasspostIssueDate(esimRegistration.getIdNoExpiredDate().format(Constant.FE_DATE_FORMATTER))
            .arrImages(buildArrImagesForUpdateMbf)
            .strBirthday(esimRegistration.getDateOfBirth().format(Constant.FE_DATE_FORMATTER))
            .strImsi(esimRegistration.getImsi())
            .strNationality(esimRegistration.getNationality())
            .countNumber(0)
            .build();
    }
}
