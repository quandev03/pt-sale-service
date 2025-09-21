package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimFreeServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.*;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.common.exception.domain.ErrorKey;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookEsimFreeService implements BookEsimFreeServicePort {

    private final BookEsimServicePort bookEsimServicePort;
    private final PackageProfileRepoPort packageProfileRepoPort;
    private final OrganizationUserRepoPort organizationUserRepositoryPort;
    private final SaleOrderRepoPort saleOrderRepoPort;


    @Override
    public BookEsimResponse bookEsim(BookEsimRequest request) {
        log.debug("[BOOK_ESIM_FREE]: Start");

        log.debug("[BOOK_ESIM_FREE]: check package profile {} is package free", request.getPackageCode());
        PackageProfileDTO packageProfileDTO= packageProfileRepoPort.findByPackageCode(request.getPackageCode());
        if(packageProfileDTO == null){
            log.error("[BOOK_ESIM_FREE]: packageProfileDTO not exits packageCode {}", request.getPackageCode());
            throw BaseException.conflictError(ErrorCode.PACKAGE_CODE_INVALID).build();
        } else if (packageProfileDTO.getPackagePrice()!=0) {
            log.error("[BOOK_ESIM_FREE]: package profile invalid");
            throw BaseException.conflictError(ErrorCode.PACKAGE_PROFILE_INVALID).build();
        }

        log.debug("[BOOK_ESIM_FREE]: Start book esim free");
        BookEsimResponse bookEsimResponse = bookEsimServicePort.bookEsim(request);
        log.debug("[BOOK_ESIM_FREE]: SUCCESS");
        return bookEsimResponse;
    }

    @Override
    public Page<SaleOrderDTO> getListBookEsimFree(Pageable pageable) {
        log.debug("[GET_BOOK_ESIM_FREE]: Start");
        log.debug("[GET_BOOK_ESIM_FREE]: check organization");
        String currentUserId = SecurityUtil.getCurrentUserId();
        OrganizationUserDTO organizationUserDTO = organizationUserRepositoryPort.findByUserId(currentUserId).orElseThrow(()-> {
            throw BaseException.conflictError(ErrorKey.BAD_REQUEST).build();
        });

        log.debug("[GET_BOOK_ESIM_FREE]: found organizationUserDTO {}", organizationUserDTO.getOrgId());

        log.debug("[GET_BOOK_ESIM_FREE]: Start getListBookEsimFree");
        Page<SaleOrderDTO> saleOrderDTO = saleOrderRepoPort.getListBookEsimFreeByOrgId(pageable, organizationUserDTO.getOrgId());

        log.debug("[GET_BOOK_ESIM_FREE]: SUCCESS");
        return saleOrderDTO;
    }
}

