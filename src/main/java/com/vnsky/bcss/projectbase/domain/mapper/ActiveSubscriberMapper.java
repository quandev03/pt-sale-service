package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.ActiveSubscriberDataDTO;
import com.vnsky.bcss.projectbase.domain.dto.AgreeDegree13DTO;
import com.vnsky.bcss.projectbase.domain.dto.GenContractDTO;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.time.LocalDate;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ActiveSubscriberMapper {

    void mapAgree13CheckBox(@MappingTarget GenContractDTO genContractData, AgreeDegree13DTO agree13);

    default GenContractDTO mapGenContractDto(ActiveSubscriberDataDTO activeData){
        LocalDate today = LocalDate.now();

        GenContractDTO genContractData = new GenContractDTO();
        mapAgree13CheckBox(genContractData, activeData.getAgreeDegree13());

        genContractData.setContractNo(UUID.randomUUID().toString());
        genContractData.setDate(today.getDayOfMonth());
        genContractData.setMonth(today.getMonth().getValue());
        genContractData.setYear(today.getYear());
        genContractData.setContractDate(today.format(Constant.FE_DATE_FORMATTER));
        genContractData.setCustomerName(activeData.getOcrData().getFullname());
        genContractData.setGender(Gender.fromValue(activeData.getOcrData().getGender()).getVietSub());
        genContractData.setBirthDate(activeData.getOcrData().getDob());
        genContractData.setIdPlace(activeData.getOcrData().getIssuedPlace());
        genContractData.setCountry(activeData.getOcrData().getNationality());
        genContractData.setCountry(activeData.getOcrData().getNationality());
        genContractData.setIdNo(activeData.getOcrData().getIdNumber());

        return genContractData;
    }

}
