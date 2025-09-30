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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<BookEsimResponse> bookEsim(List<BookEsimRequest> requests) {
        log.debug("[BOOK_ESIM_FREE]: Start processing {} requests", requests.size());

        List<BookEsimResponse> responses = new ArrayList<>();

        for (BookEsimRequest request : requests) {
            try {
                BookEsimResponse response = processSingleEsimFreeRequest(request);
                responses.add(response);
            } catch (Exception e) {
                log.error("[BOOK_ESIM_FREE]: Error processing request: {}", request, e);
                // Create error response for this request
                BookEsimResponse errorResponse = BookEsimResponse.builder()
                    .serials(new ArrayList<>())
                    .qrCodes(new ArrayList<>())
                    .status("ERROR")
                    .message("Failed to process free eSIM request: " + e.getMessage())
                    .createdDate(LocalDateTime.now())
                    .build();
                responses.add(errorResponse);
            }
        }

        log.debug("[BOOK_ESIM_FREE]: Completed processing {} requests", requests.size());
        return responses;
    }

    private BookEsimResponse processSingleEsimFreeRequest(BookEsimRequest request) {
        log.debug("[BOOK_ESIM_FREE]: Start processing single request");

        log.debug("[BOOK_ESIM_FREE]: check package profile {} is package free", request.getPackageCode());
        PackageProfileDTO packageProfileDTO = packageProfileRepoPort.findByPackageCode(request.getPackageCode());
        if (packageProfileDTO == null) {
            log.error("[BOOK_ESIM_FREE]: packageProfileDTO not exits packageCode {}", request.getPackageCode());
            throw BaseException.conflictError(ErrorCode.PACKAGE_CODE_INVALID).build();
        } else if (packageProfileDTO.getPackagePrice() != 0) {
            log.error("[BOOK_ESIM_FREE]: package profile invalid - price is not zero");
            throw BaseException.conflictError(ErrorCode.PACKAGE_PROFILE_INVALID).build();
        }

        log.debug("[BOOK_ESIM_FREE]: Start book esim free");
        List<BookEsimResponse> bookEsimResponses = bookEsimServicePort.bookEsim(List.of(request), "Book Esim Free");

        if (bookEsimResponses.isEmpty()) {
            log.error("[BOOK_ESIM_FREE]: No response received from bookEsimServicePort");
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("No response received from eSIM booking service")
                .build();
        }

        BookEsimResponse bookEsimResponse = bookEsimResponses.get(0);
        log.debug("[BOOK_ESIM_FREE]: SUCCESS");
        return bookEsimResponse;
    }

    @Override
    public Page<SaleOrderDTO> getListBookEsimFree(Pageable pageable, String toDate, String fromDate, String textSearch, int isFree) {
        log.debug("[GET_BOOK_ESIM_FREE]: Start");
        log.debug("[GET_BOOK_ESIM_FREE]: check organization");
        String currentUserId = SecurityUtil.getCurrentUserId();
        OrganizationUserDTO organizationUserDTO = organizationUserRepositoryPort.findByUserId(currentUserId).orElseThrow(() -> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());

        log.debug("[GET_BOOK_ESIM_FREE]: found organizationUserDTO {}", organizationUserDTO.getOrgId());

        log.debug("[GET_BOOK_ESIM_FREE]: Start getListBookEsimFree");
        Page<SaleOrderDTO> saleOrderDTO = saleOrderRepoPort.getListBookEsimFreeByOrgId(pageable, organizationUserDTO.getOrgId(),toDate,fromDate,textSearch, isFree);

        log.debug("[GET_BOOK_ESIM_FREE]: SUCCESS");
        return saleOrderDTO;
    }
}

