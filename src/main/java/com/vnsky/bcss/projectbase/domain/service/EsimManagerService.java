package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.EsimManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.ActionHistoryRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.MailServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.mail.MailInfoDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.ExportQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.SendQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ActionHistoryActionCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.DateUtils;
import com.vnsky.bcss.projectbase.shared.utils.QrUtils;
import com.vnsky.bcss.projectbase.shared.utils.SecurityDataUtils;
import com.vnsky.bcss.projectbase.shared.utils.XlsxUtils;
import com.vnsky.common.dto.ErrorRecord;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.common.exception.domain.ErrorKey;
import com.vnsky.excel.dto.ExcelData;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;


@Service
@Slf4j
@RequiredArgsConstructor
public class EsimManagerService implements EsimManagerServicePort {


    private final SubscriberRepoPort subscriberRepoPort;
    private final OrganizationUserRepoPort organizationUserRepoPort;
    private final ActionHistoryRepoPort actionHistoryRepoPort;
    private final MailServicePort  mailServicePort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;

    private static final String MAIL_SUBJECT_ESIM = "Thông báo đơn hàng HiVietNam";
    private static final String CONTENT_ID_MAIL_HEADER = "header-logo";
    private static final String CONTENT_ID_MAIL_BACKGROUND = "background-image";
    private static final int DEFAULT_MINUS_DAYS = 29;
    private static final String MESSAGE_TEMPLATE = "Từ ngày : {0} - Đến ngày: {1}";
    private static final String TITLE_ESIM_LIST = "Danh Sách eSIM";


    @Value("${third-party.qr-code.create-qr-code-url}")
    private String createOrCodeUrl;

    @Value("${application.mail-header-logo-path}")
    private String mailHeaderLogoPath;

    @Value("${application.mail-header-background-path}")
    private String mailBackground;


    @Override
    public Page<EsimInforDTO> getListEsimInforPartnerDTO(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate, Pageable pageable) {
        log.debug("[GET_LIST_ESIM]: Start get list esim infor partner ");
        String currentUserId = SecurityUtil.getCurrentUserId();
        OrganizationUserDTO organizationUserDTO = organizationUserRepoPort.findByUserId(currentUserId).orElseThrow(()-> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());
        log.info("[GET_LIST_ESIM]: id partner: {}", organizationUserDTO.getOrgId());

        fromDate = formatInputFromDate(fromDate);
        toDate = formatInputToDate(toDate);

        return subscriberRepoPort.getListEsimInfor(textSearch, subStatus, activeStatus, pckCode, organizationUserDTO.getOrgId(), orgId, fromDate, toDate, pageable);
    }

    @Override
    public List<ActionHistoryDTO> getActionHistoryDTO(String id) {
        log.debug("[GET_ACTION_HISTORY]: Start get action history");
        SubscriberDTO subscriberDTO = subscriberRepoPort.findById(id).orElseThrow(()-> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());
        log.info("[GET_ACTION_HISTORY]: find subscriber {}", subscriberDTO.getId());
        List<ActionHistoryDTO> listActionHisTory = actionHistoryRepoPort.getListActionHistoryBySubId(subscriberDTO.getId(), Sort.by(Sort.Direction.DESC, "created_date"));
        log.info("[GET_ACTION_HISTORY]: end get action history, find: {}", listActionHisTory.size());
        return listActionHisTory;
    }

    @Override
    @Transactional
    public void sendMailEsim(SendQrCodeRequest request) {
        String genQrBy = SecurityUtil.getCurrentPreferredUsername();
        SubscriberDTO subscriberDTO = subscriberRepoPort.findById(request.getSubId()).orElseThrow(()-> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());
        log.info("[ACTION_HISTORY]: get qr code:: {}", subscriberDTO.getGenQrBy());
        log.info("[ACTION_HISTORY]: Current user: {}", genQrBy);
        if (!Objects.isNull(subscriberDTO.getGenQrBy())  && !Objects.equals(subscriberDTO.getGenQrBy(), genQrBy)) {
            throw BaseException.conflictError(ErrorCode.USER_NOT_PERMISSION).build();
        }

        log.info("[SEND_MAIL_ESIM]: Start send mail");

        log.info("Received request send mail esim with subcriber: {}, serial {}, email {}", request.getSubId(), subscriberDTO.getSerial(), request.getEmail());
        MailInfoDTO mailInfo = MailInfoDTO.builder()
            .subject(MAIL_SUBJECT_ESIM)
            .to(request.getEmail())
            .isdn(String.valueOf(subscriberDTO.getIsdn()))
            .serial(subscriberDTO.getSerial())
            .urlQR(createOrCodeUrl.concat(Objects.requireNonNull(SecurityDataUtils.encryptAndEncodeUrl(subscriberDTO.getLpa(), SecurityDataUtils.PUBLIC_KEY_FIX))))
            .imageCids(List.of(
                MailInfoDTO.FileCid.builder()
                .contentId(CONTENT_ID_MAIL_HEADER)
                .path(mailHeaderLogoPath)
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .build(),
                MailInfoDTO.FileCid.builder()
                    .contentId(CONTENT_ID_MAIL_BACKGROUND)
                    .path(mailBackground)
                    .contentType(MediaType.IMAGE_PNG_VALUE)
                    .build()
            ))
            .build();
        mailServicePort.sendMail(mailInfo, "EmailEsim");
        log.debug("[SEND_MAIL_ESIM]: send qr code success");

        ActionHistoryDTO actionHistoryDTO = ActionHistoryDTO.builder()
            .subId(subscriberDTO.getId())
            .actionDate(LocalDateTime.now())
            .actionCode(ActionHistoryActionCode.SEND_QR_CODE.getCode())
            .reasonCode(ActionHistoryActionCode.ACTION_TYPE.getCode())
            .build();
        actionHistoryDTO = actionHistoryRepoPort.save(actionHistoryDTO);


        if (Objects.isNull(actionHistoryDTO.getId())) {
            log.error("[SEND_MAIL_ESIM]: action id is null");
        }

        subscriberDTO.setGenQrBy(genQrBy);
        subscriberDTO.setBoughtStatus(1);
        subscriberRepoPort.saveAndFlush(subscriberDTO);
        log.info("[SEND_MAIL_ESIM]: save send qr code by: {}", genQrBy);
        log.info("[SEND_MAIL_ESIM]: Send QR code success");
    }

    @Override
    public Page<EsimInforDTO> getListEsimInforDTO(String textSearch, Integer subStatus, Integer activeStatus, String pckCode,List<String> orgId, String fromDate, String toDate, Pageable pageable) {
        log.debug("[GET_LIST_ESIM]: Start get list esim infor internal ");
        fromDate = formatInputFromDate(fromDate);
        toDate = formatInputToDate(toDate);
        log.info("[FORMAT_DATE] From date: {}, To date: {}", fromDate, toDate);

        return subscriberRepoPort.getListEsimInforInternal(textSearch, subStatus, activeStatus, pckCode, orgId, fromDate, toDate, pageable);
    }

    @Override
    public List<OrganizationUnitResponse> getListOrganization() {
        return organizationUnitRepoPort.getListOrganization();
    }

    @Override
    @Transactional
    public Resource esimGenerateQR(String subId, String size) {

        String genQrBy = SecurityUtil.getCurrentPreferredUsername();

        SubscriberDTO subscriberDTO= subscriberRepoPort.findById(subId).orElseThrow(()-> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());
        log.info("[ACTION_HISTORY]: get qr code:: {}", subscriberDTO.getGenQrBy());
        log.info("[ACTION_HISTORY]: Current user: {}", genQrBy);
        if (!Objects.equals(subscriberDTO.getGenQrBy(), genQrBy) && subscriberDTO.getGenQrBy() != null) {
            throw BaseException.conflictError(ErrorCode.USER_NOT_PERMISSION).build();
        }

        OrganizationUserDTO orgUser = organizationUserRepoPort.findByUserId(SecurityUtil.getCurrentUserId()).orElseThrow(()-> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());

        if (Objects.isNull(size)) {
            size = "640x640";
        }
        if(!Objects.equals(subscriberRepoPort.isEsimBelongToAgent(subscriberDTO.getIsdn(),orgUser.getOrgId()), 1)) {
            throw BaseException.conflictError(ErrorCode.USER_NOT_PERMISSION).build();
        }
        try {
            String[] sizes = size.split("x");
            int width = Integer.parseInt(sizes[0]);
            int height = Integer.parseInt(sizes[1]);
            byte[] resourceByte = QrUtils.generateQrCode(subscriberDTO.getLpa(), width, height);

            ActionHistoryDTO actionHistoryDTO = ActionHistoryDTO.builder()
                .subId(subId)
                .actionDate(LocalDateTime.now())
                .actionCode(ActionHistoryActionCode.GEN_QR_CODE.getCode())
                .reasonCode(ActionHistoryActionCode.ACTION_TYPE.getCode())
                .build();
            actionHistoryDTO = actionHistoryRepoPort.save(actionHistoryDTO);

            if (Objects.isNull(actionHistoryDTO.getId())) {
                log.error("[SEND_MAIL_ESIM]: action id is null");
            }

            subscriberDTO.setGenQrBy(genQrBy);
            subscriberDTO.setBoughtStatus(1);
            subscriberRepoPort.saveAndFlush(subscriberDTO);
            log.info("[SEND_MAIL_ESIM]: save send qr code by: {}", genQrBy);

            return new ByteArrayResource(resourceByte);
        }catch (Exception e) {
            log.error("exc",e);
            throw BaseException.badRequest(ErrorKey.INTERNAL_SERVER_ERROR).message("Status cannot export").build();
        }
    }

    @Override
    public ESimDetailResponse detailEsim(String subId) {
        log.info("[GET_DETAIL_ESIM]: Start detail esim infor partner ");
        log.info("[GET_DETAIL_ESIM]: id subscriber: {}", subId);
        ESimDetailResponse subscriberDTO = subscriberRepoPort.findEsimDetailById(subId);
        log.info("[GET_DETAIL_ESIM]: id subscriber: {}", subscriberDTO);
        return subscriberDTO;
    }

    @Override
    public Resource esimGenerateQRCode(String data, String size) {
        try {
            String[] sizes = size.split("x");
            String decryptedLPA = SecurityDataUtils.decodeUrlAndDecrypt(data, SecurityDataUtils.PRIVATE_KEY_FIX);
            int width = Integer.parseInt(sizes[0]);
            int height = Integer.parseInt(sizes[1]);
            byte[] resourceByte = QrUtils.generateQrCode(decryptedLPA, width, height);
            return new ByteArrayResource(resourceByte);
        }catch (Exception e) {
            throw BaseException.badRequest(ErrorKey.INTERNAL_SERVER_ERROR).message("Status cannot export").build();
        }
    }

    @Override
    public List<OrganizationUnitResponse> getListOrganizationUnit() {
        String currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[GET_ORG_CHILD]: current user id: {}", currentUserId);
        OrganizationUserDTO organizationUserDTO = organizationUserRepoPort.findByUserId(currentUserId).orElseThrow(()-> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());
        return organizationUnitRepoPort.getListOrganizationUnitChild(organizationUserDTO.getOrgId());
    }

    @Override
    @Transactional
    public Resource exportListEsimExcel(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate) {
        log.debug("[EXPORT_LIST_ESIM]: Start get list esim infor partner ");
        String currentUserId = SecurityUtil.getCurrentUserId();
        OrganizationUserDTO organizationUserDTO = organizationUserRepoPort.findByUserId(currentUserId).orElseThrow(()-> BaseException.conflictError(ErrorKey.BAD_REQUEST).build());
        log.info("[EXPORT_LIST_ESIM]: id partner: {}", organizationUserDTO.getOrgId());

        fromDate = formatInputFromDate(fromDate);
        toDate = formatInputToDate(toDate);
        validateTime(fromDate, toDate);

        List<EsimInforDTO> listEsimInfor = subscriberRepoPort.getListEsimInforExport(textSearch, subStatus, activeStatus, pckCode, organizationUserDTO.getOrgId(), orgId, fromDate, toDate);

        // Cập nhật bought_status = 1 cho tất cả subscriber được export bằng batch save
        List<String> subIds = listEsimInfor.stream()
            .map(EsimInforDTO::getSubId)
            .toList();

        if (!subIds.isEmpty()) {
            List<SubscriberDTO> subscribersToUpdate = subscriberRepoPort.findByIds(subIds);
            subscribersToUpdate.forEach(subscriber -> subscriber.setBoughtStatus(1));
            subscriberRepoPort.saveAllAndFlush(subscribersToUpdate);
            log.info("[EXPORT_LIST_ESIM]: Updated bought_status for {} subscribers", subscribersToUpdate.size());

            // Lưu action history cho export excel (đánh dấu VIEW)
            List<ActionHistoryDTO> excelExportHistories = subIds.stream()
                .map(id -> ActionHistoryDTO.builder()
                    .subId(id)
                    .actionDate(LocalDateTime.now())
                    .actionCode(ActionHistoryActionCode.ACTION_TYPE.getCode())
                    .createdBy(SecurityUtil.getCurrentPreferredUsername())
                    .createdDate(LocalDateTime.now())
                    .build())
                .toList();
            actionHistoryRepoPort.saveAllAndFlush(excelExportHistories);

            // Refetch danh sách sau khi cập nhật để export dùng dữ liệu mới nhất
            listEsimInfor = subscriberRepoPort.getListEsimInforExport(textSearch, subStatus, activeStatus, pckCode, organizationUserDTO.getOrgId(), orgId, fromDate, toDate);
        }

        List<ExportEsimExcelDTO> exportEsimExcelDTOS = new ArrayList<>();
        listEsimInfor.forEach(esimInfor -> {
            ExportEsimExcelDTO exportEsimExcelDTO = ExportEsimExcelDTO.builder()
                .orderNo(esimInfor.getOrderNo())
                .orgName(esimInfor.getOrgName())
                .modifiedDate(esimInfor.getModifiedDate().format(DateTimeFormatter.ofPattern(Constant.TIME_STAMP_FE_DATE)))
                .activeStatus(convertActiveStatus(esimInfor.getActiveStatus()))
                .genQrBy(esimInfor.getGenQrBy())
                .packageCode(esimInfor.getPackCode())
                .isdn(esimInfor.getIsdn())
                .lpaCode(esimInfor.getLpaCode())
                .serial(esimInfor.getSerial())
                .subStatus(convertSubStatus(esimInfor.getStatusSub()))
                .createdDate(esimInfor.getCreatedDate().format(DateTimeFormatter.ofPattern(Constant.TIME_STAMP_FE_DATE)))
                .build();

            exportEsimExcelDTOS.add(exportEsimExcelDTO);
        });

        Function<String, byte[]> imageConverter = lpaCode -> QrUtils.generateQrCode(lpaCode, 200, 200);
        String time = MessageFormat.format(MESSAGE_TEMPLATE, fromDate, toDate);
        return XlsxUtils.writeExcelHaveImage(new ExcelData<>(new HashMap<>(), exportEsimExcelDTOS, true), ExportEsimExcelDTO.class, imageConverter, false, TITLE_ESIM_LIST, time);
    }

    @Override
    public Resource exportListEsimExcelInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate) {
        log.debug("[EXPORT_LIST_ESIM_INTERNAL]: Start get list esim infor partner ");

        fromDate = formatInputFromDate(fromDate);
        toDate = formatInputToDate(toDate);
        validateTime(fromDate, toDate);

        List<EsimInforDTO> listEsimInfor = subscriberRepoPort.getListEsimInforExportInternal(textSearch, subStatus, activeStatus, pckCode, orgId, fromDate, toDate);
        List<ExportEsimExcelInternalDTO> exportEsimExcelDTOS = new ArrayList<>();
        listEsimInfor.forEach(esimInfor -> {
            ExportEsimExcelInternalDTO exportEsimExcelDTO = ExportEsimExcelInternalDTO.builder()
                    .orderNo(esimInfor.getOrderNo())
                    .orgName(esimInfor.getOrgName())
                    .modifiedDate(DateUtils.localDateTimeToString(esimInfor.getModifiedDate(), Constant.TIME_STAMP_FE_DATE))
                    .activeStatus(convertActiveStatus(esimInfor.getActiveStatus()))
                    .genQrBy(esimInfor.getGenQrBy())
                    .packageCode(esimInfor.getPackCode())
                    .isdn(esimInfor.getIsdn())
                    .serial(esimInfor.getSerial())
                    .subStatus(convertSubStatus(esimInfor.getStatusSub()))
                    .createdDate(esimInfor.getCreatedDate().format(DateTimeFormatter.ofPattern(Constant.TIME_STAMP_FE_DATE)))
                    .build();
            exportEsimExcelDTOS.add(exportEsimExcelDTO);
        });

        return XlsxUtils.writeExcel(new ExcelData<>(new HashMap<>(), exportEsimExcelDTOS, true), ExportEsimExcelInternalDTO.class, false, TITLE_ESIM_LIST, MessageFormat.format(MESSAGE_TEMPLATE, fromDate, toDate));
    }

    @Override
    @Transactional
    public Resource exportListQrCode(ExportQrCodeRequest request) {
        // Cập nhật bought_status = 1 cho tất cả subscriber được export bằng batch save
        List<String> subIds = request.getSubIds();

        if (!subIds.isEmpty()) {
            List<SubscriberDTO> subscribersToUpdate = subscriberRepoPort.findByIds(subIds);
            subscribersToUpdate.forEach(subscriber -> subscriber.setBoughtStatus(1));
            subscriberRepoPort.saveAllAndFlush(subscribersToUpdate);
            log.info("[EXPORT_QR_CODE]: Updated bought_status for {} subscribers", subscribersToUpdate.size());

            // Lưu action history cho export QR (tạo mã QR)
            List<ActionHistoryDTO> qrExportHistories = subIds.stream()
                .map(id -> ActionHistoryDTO.builder()
                    .subId(id)
                    .actionDate(LocalDateTime.now())
                    .actionCode(ActionHistoryActionCode.GEN_QR_CODE.getCode())
                    .createdBy(SecurityUtil.getCurrentPreferredUsername())
                    .createdDate(LocalDateTime.now())
                    .build())
                .toList();
            actionHistoryRepoPort.saveAllAndFlush(qrExportHistories);
        }

        //export
        List<ExportQrEsimExcel> exportQrEsimExcels = subscriberRepoPort.getListEsimQrCode(request.getSubIds());
        Function<String, byte[]> imageConverter = lpa -> QrUtils.generateQrCode(lpa, 150, 150);
        return XlsxUtils.writeExcelHaveImage(new ExcelData<>(new HashMap<>(), exportQrEsimExcels, false), ExportQrEsimExcel.class, imageConverter, false, null, null);
    }

    private String convertActiveStatus(int activeStatus){
        if(activeStatus == 1) return Constant.ActiveStatusSub.ACTIVE;
        else if (activeStatus == 10) return Constant.ActiveStatusSub.BLOCK_OUTGOING_BY_REQUEST;
        else if (activeStatus == 11) return Constant.ActiveStatusSub.BLOCK_OUTGOING_BY_OPERATOR;
        else if (activeStatus == 20) return Constant.ActiveStatusSub.BLOCK_BOTH_BY_REQUEST;
        else if (activeStatus == 21) return Constant.ActiveStatusSub.BLOCK_BOTH_BY_OPERATOR;
        return null;
    }
    private String convertSubStatus(int activeStatus){
        if(activeStatus == 0) return Constant.StatusSub.IN_STOCK;
        else if (activeStatus == 1) return Constant.StatusSub.SOLD;
        else if (activeStatus == 2) return Constant.StatusSub.CALL_900;
        else if (activeStatus == 3) return Constant.StatusSub.UPDATE_INFO;
        return null;
    }

    private String formatInputFromDate(String fromDate){
        LocalDateTime currentDate = LocalDateTime.now();
        if(fromDate == null || fromDate.isBlank()){
            fromDate = currentDate.minusDays(DEFAULT_MINUS_DAYS).format(DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN));
        }
        log.info("[FORMAT_DATE] From date: {}", fromDate);
        return fromDate;
    }

    private String formatInputToDate(String toDate){
        LocalDateTime currentDate = LocalDateTime.now();
        if(toDate == null || toDate.isBlank()){
            toDate = currentDate.format(DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN));
        }
        log.info("[FORMAT_DATE] To date: {}", toDate);
        return toDate;
    }
    private void validateTime(String fromDate, String toDate){
        LocalDate from =  LocalDate.parse(fromDate, DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN));
        LocalDate to =  LocalDate.parse(toDate, DateTimeFormatter.ofPattern(Constant.FE_DATE_PATTERN));
        if(from.isAfter(to)) throw BaseException.bussinessError(ErrorCode.TIME_INVALID).addProperty(new ErrorRecord("Thời gian bắt đầu phải nhỏ hơn hoặc bằng thời gian kết thúc", "fromDate")).build();

    }
}
