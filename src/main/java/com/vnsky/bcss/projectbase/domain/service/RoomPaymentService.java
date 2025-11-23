package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.RoomPaymentServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomPaymentRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomServiceRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.MailServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.VietQRPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.mail.MailInfoDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.RoomServiceUsageDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.VietQRResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import com.vnsky.bcss.projectbase.shared.utils.XlsxUtils;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomPaymentService implements RoomPaymentServicePort {

    private final RoomPaymentRepoPort roomPaymentRepoPort;
    private final RoomServiceRepoPort roomServiceRepoPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;
    private final OrganizationUserRepoPort organizationUserRepoPort;
    private final VietQRPort vietQRPort;
    private final MailServicePort mailServicePort;

    private static final String LOG_PREFIX = "[RoomPaymentService]_";

    @Override
    @Transactional
    public List<RoomPaymentDTO> processExcelAndCreatePayments(MultipartFile file, Integer month, Integer year) {
        log.info("{}Processing Excel file for month: {}/{}, file: {}", LOG_PREFIX, month, year, file.getOriginalFilename());

        // Đọc Excel file
        List<RoomServiceUsageDTO> usageData;
        try {
            usageData = XlsxUtils.readExcel(file.getInputStream(), RoomServiceUsageDTO.class).getDataLines();
        } catch (IOException e) {
            log.error("{}Error reading Excel file: {}", LOG_PREFIX, e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Lỗi đọc file Excel: " + e.getMessage())
                .build();
        }

        if (usageData == null || usageData.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("File Excel không có dữ liệu")
                .build();
        }

        List<RoomPaymentDTO> createdPayments = new ArrayList<>();

        // Xử lý từng phòng
        for (RoomServiceUsageDTO usage : usageData) {
            if (StringUtils.isBlank(usage.getRoomCode())) {
                log.warn("{}Skipping row with empty room code", LOG_PREFIX);
                continue;
            }

            try {
                RoomPaymentDTO payment = processRoomPayment(usage, month, year);
                if (payment != null) {
                    createdPayments.add(payment);
                }
            } catch (Exception e) {
                log.error("{}Error processing room {}: {}", LOG_PREFIX, usage.getRoomCode(), e.getMessage(), e);
                // Continue với phòng tiếp theo
            }
        }

        log.info("{}Processed {} payments", LOG_PREFIX, createdPayments.size());
        return createdPayments;
    }

    private RoomPaymentDTO processRoomPayment(RoomServiceUsageDTO usage, Integer month, Integer year) {
        // Tìm organization unit theo roomCode
        OrganizationUnitDTO orgUnit = organizationUnitRepoPort.findByCode(usage.getRoomCode())
            .orElseThrow(() -> {
                log.error("{}Organization unit not found for room code: {}", LOG_PREFIX, usage.getRoomCode());
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Không tìm thấy phòng với mã: " + usage.getRoomCode())
                    .build();
            });

        // Kiểm tra đã có thanh toán cho tháng/năm này chưa
        Optional<RoomPaymentDTO> existing = roomPaymentRepoPort.findByOrgUnitIdAndMonthAndYear(
            orgUnit.getId(), month, year);
        if (existing.isPresent()) {
            log.warn("{}Payment already exists for room {} month {}/{}, skipping", 
                LOG_PREFIX, usage.getRoomCode(), month, year);
            return existing.get();
        }

        // Lấy tất cả dịch vụ phòng
        List<RoomServiceDTO> roomServices = roomServiceRepoPort.findByOrgUnitId(orgUnit.getId());

        // Tính toán tiền
        List<RoomPaymentDetailDTO> details = calculatePaymentDetails(usage, roomServices, orgUnit);
        BigDecimal totalAmount = details.stream()
            .map(RoomPaymentDetailDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo QR code
        String qrCodeUrl = null;
        byte[] qrCodeImage = null;
        if (orgUnit.getOrgBankAccountNo() != null && orgUnit.getBankName() != null) {
            String bankCode = extractBankCode(orgUnit.getBankName());
            String content = String.format("Phòng %s đóng tiền phòng tháng %d/%d", 
                usage.getRoomCode(), month, year);
            
            VietQRResponse qrResponse = vietQRPort.generateQRCode(
                orgUnit.getOrgBankAccountNo(),
                orgUnit.getOrgName() != null ? orgUnit.getOrgName() : "",
                totalAmount,
                content,
                bankCode
            );

            if (qrResponse != null && qrResponse.getData() != null) {
                qrCodeUrl = qrResponse.getData().getQrDataURL();
                // Có thể download QR code image từ URL nếu cần
            }
        }

        // Lưu payment
        RoomPaymentDTO payment = RoomPaymentDTO.builder()
            .orgUnitId(orgUnit.getId())
            .month(month)
            .year(year)
            .totalAmount(totalAmount)
            .qrCodeUrl(qrCodeUrl)
            .qrCodeImage(qrCodeImage)
            .status(0) // Chưa thanh toán
            .build();

        RoomPaymentDTO savedPayment = roomPaymentRepoPort.save(payment);
        
        // Lưu details
        for (RoomPaymentDetailDTO detail : details) {
            detail.setRoomPaymentId(savedPayment.getId());
            roomPaymentRepoPort.saveDetail(detail);
        }

        // Load lại details để gửi email
        savedPayment.setDetails(roomPaymentRepoPort.findDetailsByRoomPaymentId(savedPayment.getId()));

        // Gửi email
        sendPaymentEmail(savedPayment, orgUnit, usage.getRoomCode());

        return savedPayment;
    }

    private List<RoomPaymentDetailDTO> calculatePaymentDetails(
        RoomServiceUsageDTO usage, 
        List<RoomServiceDTO> roomServices,
        OrganizationUnitDTO orgUnit) {
        
        List<RoomPaymentDetailDTO> details = new ArrayList<>();
        Map<RoomServiceType, RoomServiceDTO> serviceMap = roomServices.stream()
            .filter(rs -> rs.getStatus() != null && rs.getStatus().equals(Status.ACTIVE.getValue()))
            .collect(Collectors.toMap(RoomServiceDTO::getServiceType, rs -> rs, (a, b) -> a));

        // Tính tiền điện
        if (usage.getElectricityUsage() != null && usage.getElectricityUsage().compareTo(BigDecimal.ZERO) > 0) {
            RoomServiceDTO electricityService = serviceMap.get(RoomServiceType.ELECTRICITY);
            if (electricityService != null) {
                BigDecimal amount = usage.getElectricityUsage().multiply(electricityService.getPrice());
                details.add(createDetail(electricityService, usage.getElectricityUsage(), amount));
            }
        }

        // Tính tiền nước
        if (usage.getWaterUsage() != null && usage.getWaterUsage().compareTo(BigDecimal.ZERO) > 0) {
            RoomServiceDTO waterService = serviceMap.get(RoomServiceType.WATER);
            if (waterService != null) {
                BigDecimal amount = usage.getWaterUsage().multiply(waterService.getPrice());
                details.add(createDetail(waterService, usage.getWaterUsage(), amount));
            }
        }

        // Tính tiền xe - tìm dịch vụ có serviceCode chứa "XE" hoặc "VEHICLE"
        if (usage.getVehicleCount() != null && usage.getVehicleCount().compareTo(BigDecimal.ZERO) > 0) {
            RoomServiceDTO vehicleService = roomServices.stream()
                .filter(rs -> rs.getServiceCode() != null && 
                    (rs.getServiceCode().toUpperCase().contains("XE") || 
                     rs.getServiceCode().toUpperCase().contains("VEHICLE")))
                .findFirst()
                .orElse(null);

            if (vehicleService != null) {
                BigDecimal amount = usage.getVehicleCount().multiply(vehicleService.getPrice());
                details.add(createDetail(vehicleService, usage.getVehicleCount(), amount));
            }
        }

        // Thêm tiền phòng
        if (orgUnit.getPriceRoom() != null && orgUnit.getPriceRoom().compareTo(BigDecimal.ZERO) > 0) {
            RoomPaymentDetailDTO roomRentDetail = RoomPaymentDetailDTO.builder()
                .serviceType(RoomServiceType.ROOM_RENT)
                .serviceName("Tiền phòng")
                .quantity(BigDecimal.ONE)
                .unitPrice(orgUnit.getPriceRoom())
                .amount(orgUnit.getPriceRoom())
                .build();
            details.add(roomRentDetail);
        }

        // Thêm các dịch vụ khác (INTERNET, OTHER) nếu có
        for (RoomServiceDTO service : roomServices) {
            if (service.getServiceType() == RoomServiceType.INTERNET || 
                service.getServiceType() == RoomServiceType.OTHER) {
                // Dịch vụ cố định, không tính theo số lượng
                details.add(createDetail(service, BigDecimal.ONE, service.getPrice()));
            }
        }

        return details;
    }

    private RoomPaymentDetailDTO createDetail(RoomServiceDTO service, BigDecimal quantity, BigDecimal amount) {
        return RoomPaymentDetailDTO.builder()
            .roomServiceId(service.getId())
            .serviceType(service.getServiceType())
            .serviceName(service.getServiceName())
            .quantity(quantity)
            .unitPrice(service.getPrice())
            .amount(amount)
            .build();
    }

    private String extractBankCode(String bankName) {
        if (bankName == null) {
            return null;
        }
        // Map một số ngân hàng phổ biến
        Map<String, String> bankCodeMap = Map.of(
            "Vietcombank", "970415",
            "BIDV", "970415",
            "Vietinbank", "970415",
            "Agribank", "970415",
            "Techcombank", "970415",
            "ACB", "970415",
            "VPBank", "970415",
            "TPBank", "970415",
            "MBBank", "970415",
            "Sacombank", "970415"
        );
        // Tìm theo tên ngân hàng (case insensitive)
        return bankCodeMap.entrySet().stream()
            .filter(entry -> bankName.toUpperCase().contains(entry.getKey().toUpperCase()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);
    }

    private void sendPaymentEmail(RoomPaymentDTO payment, OrganizationUnitDTO orgUnit, String roomCode) {
        // Lấy danh sách user của phòng
        List<OrganizationUserDTO> users = organizationUserRepoPort.getAllOrganizationUserByOrgId(orgUnit.getId());
        
        if (users == null || users.isEmpty()) {
            log.warn("{}No users found for room: {}", LOG_PREFIX, roomCode);
            return;
        }

        // Lấy chi tiết payment
        List<RoomPaymentDetailDTO> details = roomPaymentRepoPort.findDetailsByRoomPaymentId(payment.getId());
        payment.setDetails(details);

        // Gửi email cho từng user
        for (OrganizationUserDTO user : users) {
            if (StringUtils.isBlank(user.getEmail())) {
                continue;
            }

            try {
                // Chuyển đổi details sang format cho email
                List<MailInfoDTO.RoomPaymentDetailInfo> detailInfos = details.stream()
                    .map(detail -> MailInfoDTO.RoomPaymentDetailInfo.builder()
                        .serviceName(detail.getServiceName())
                        .quantity(detail.getQuantity() != null ? detail.getQuantity().toString() : "0")
                        .unitPrice(detail.getUnitPrice() != null ? detail.getUnitPrice().toString() : "0")
                        .amount(detail.getAmount() != null ? detail.getAmount().toString() : "0")
                        .build())
                    .collect(java.util.stream.Collectors.toList());

                // Tạo MailInfoDTO với thông tin cần thiết
                MailInfoDTO mailInfo = MailInfoDTO.builder()
                    .to(user.getEmail())
                    .subject(String.format("Hóa đơn dịch vụ phòng %s - Tháng %d/%d", 
                        roomCode, payment.getMonth(), payment.getYear()))
                    .amount(payment.getTotalAmount().toString())
                    .name(user.getUserFullname() != null ? user.getUserFullname() : "")
                    .urlQR(payment.getQrCodeUrl())
                    .roomCode(roomCode)
                    .month(payment.getMonth())
                    .year(payment.getYear())
                    .roomPaymentDetails(detailInfos)
                    .build();

                mailServicePort.sendMail(mailInfo, "RoomPaymentInvoice");
                log.info("{}Email sent to: {}", LOG_PREFIX, user.getEmail());
            } catch (Exception e) {
                log.error("{}Error sending email to {}: {}", LOG_PREFIX, user.getEmail(), e.getMessage(), e);
            }
        }
    }

    @Override
    public RoomPaymentDTO getById(String id) {
        RoomPaymentDTO payment = roomPaymentRepoPort.findById(id)
            .orElseThrow(() -> {
                log.error("{}Room payment not found: {}", LOG_PREFIX, id);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Không tìm thấy thông tin thanh toán")
                    .build();
            });
        // Load details
        List<RoomPaymentDetailDTO> details = roomPaymentRepoPort.findDetailsByRoomPaymentId(payment.getId());
        payment.setDetails(details);
        return payment;
    }

    @Override
    public List<RoomPaymentDTO> getAll(String orgUnitId, Integer year, Integer month) {
        List<RoomPaymentDTO> payments;
        
        if (orgUnitId != null) {
            // Lấy theo orgUnitId
            payments = roomPaymentRepoPort.findByFilters(orgUnitId, year, month);
        } else {
            // Lấy tất cả payments của client hiện tại
            String clientId = SecurityUtil.getCurrentClientId();
            payments = roomPaymentRepoPort.findByClientId(clientId, year, month);
        }
        
        // Load details cho mỗi payment
        payments.forEach(payment -> {
            List<RoomPaymentDetailDTO> details = roomPaymentRepoPort.findDetailsByRoomPaymentId(payment.getId());
            payment.setDetails(details);
        });
        
        return payments;
    }
}

