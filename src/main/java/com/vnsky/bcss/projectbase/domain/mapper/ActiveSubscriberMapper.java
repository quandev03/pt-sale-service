package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.ActiveSubscriberDataDTO;
import com.vnsky.bcss.projectbase.domain.dto.AgreeDecree13ContractDataDto;
import com.vnsky.bcss.projectbase.domain.dto.AgreeDecree13DTO;
import com.vnsky.bcss.projectbase.domain.dto.GenContractDTO;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.BeanUtils;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface ActiveSubscriberMapper {

    void mapAgree13CheckBox(@MappingTarget AgreeDecree13ContractDataDto genContractData, AgreeDecree13DTO agree13);

    default AgreeDecree13ContractDataDto mapAgree13DFromGenContract(GenContractDTO genContractData, AgreeDecree13DTO agree13){
        AgreeDecree13ContractDataDto data = new AgreeDecree13ContractDataDto();
        BeanUtils.copyProperties(genContractData, data);
        mapAgree13CheckBox(data, agree13);

        return data;
    }

    default GenContractDTO mapGenContractDto(ActiveSubscriberDataDTO activeData){
        LocalDate today = LocalDate.now();

        GenContractDTO genContractData = new GenContractDTO();

        genContractData.setCustomerId(activeData.getCustomerCode());
        genContractData.setContractNo(activeData.getContractCode());
        genContractData.setDate(today.getDayOfMonth());
        genContractData.setMonth(today.getMonth().getValue());
        genContractData.setYear(today.getYear());
        genContractData.setContractDate(today.format(Constant.FE_DATE_FORMATTER));
        genContractData.setCustomerName(activeData.getOcrData().getFullname());
        genContractData.setGender(Gender.fromValue(activeData.getOcrData().getGender()).getVietSub());
        genContractData.setGenderEn(activeData.getOcrData().getGender());
        genContractData.setBirthDate(activeData.getOcrData().getDob());
        genContractData.setIdPlace(activeData.getOcrData().getIssuedPlace());
        genContractData.setCountry(activeData.getOcrData().getNationality());
        genContractData.setIdNo(activeData.getOcrData().getIdNumber());
        genContractData.setEmployeeName(activeData.getEmployeeFullName());
        genContractData.setIsdn("0" + activeData.getIsdn());

        return genContractData;
    }

}
