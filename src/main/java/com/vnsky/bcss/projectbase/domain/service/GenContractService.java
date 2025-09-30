package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.config.ParamContractConfig;
import com.vnsky.bcss.projectbase.domain.dto.ActiveSubscriberDataDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.GenContractServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenContractService implements GenContractServicePort {
    private final SubscriberRepoPort subscriberRepoPort;
    private final ParamContractConfig paramContractConfig;
    private final ApplicationContext applicationContext;

    @Override
    @Transactional
    public void genCustomerCode(ActiveSubscriberDataDTO activeData, SubscriberDTO subscriber) {
        if(subscriber.getContractCode() == null){
            log.info("Subscriber with serial {} don't have customer code", activeData.getSerial());

            //Phải goi qua dependency thì retry mới hoạt động
            GenContractService self = applicationContext.getBean(GenContractService.class);

            subscriber.setContractCode(self.generateContractCodeWithRetry(activeData.getIsdn(), paramContractConfig.getContract().getSimActive().getContractCodeFormat()));
            subscriber.setCustomerCode(self.generateCustomerCodeWithRetry(activeData.getIsdn(), paramContractConfig.getContract().getSimActive().getCustomerCodeFormat()));

            log.info("Save customerCode {}, contractCode {} for subscriber with serial {}", subscriber.getCustomerCode(), subscriber.getContractCode(), activeData.getSerial());
            subscriber = subscriberRepoPort.saveAndFlush(subscriber);
        }

        activeData.setCustomerCode(subscriber.getCustomerCode());
        activeData.setContractCode(subscriber.getContractCode());
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 100, backoff = @Backoff(delay = 100))
    public String generateContractCodeWithRetry(Long isdn, String format){
        log.info("[GEN_CONTRACT]: Gen contract code for isdn {}", isdn);

        long code = Math.round(Math.random() * 100000000L);
        String contractCode = String.format(format, code);

        if(subscriberRepoPort.isExistByContractCodeOrCustomerCode(contractCode, null)){
            log.info("[GEN_CONTRACT]: Existed contractCode {}", contractCode);
            throw BaseException.badRequest(ErrorCode.CUSTOMER_CODE_EXISTED)
                .message("Contract code is existed, retry again")
                .build();
        }
        return contractCode;
    }


    @Retryable(retryFor = Exception.class, maxAttempts = 100, backoff = @Backoff(delay = 100))
    public String generateCustomerCodeWithRetry(Long isdn, String format){
        log.info("[GEN_CONTRACT]: Gen customer code for isdn {}", isdn);

        long code = Math.round(Math.random() * 10000000000L);
        String customerCode = String.format(format, code);

        if(subscriberRepoPort.isExistByContractCodeOrCustomerCode(null, customerCode)){
            log.info("[GEN_CONTRACT]: Existed customerCode {}", customerCode);
            throw BaseException.badRequest(ErrorCode.CUSTOMER_CODE_EXISTED)
                .message("Contract code is existed, retry again")
                .build();
        }

        return customerCode;
    }
}
