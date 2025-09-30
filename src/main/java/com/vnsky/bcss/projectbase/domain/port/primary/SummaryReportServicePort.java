package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.SummaryAllReportDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryByOrgReportResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

/**
 * Port cho service báo cáo tổng hợp
 */
public interface SummaryReportServicePort {

    /**
     * Tìm kiếm báo cáo tổng hợp theo từng tổ chức
     * @param startDate Ngày bắt đầu (yyyy-MM-dd)
     * @param endDate Ngày kết thúc (yyyy-MM-dd)
     * @param pageable Thông tin phân trang
     * @return Response chứa dữ liệu phân trang và tổng cho mỗi field
     */
    SummaryByOrgReportResponseDTO searchSummaryByOrgReport(String startDate, String endDate, Pageable pageable);

    /**
     * Tìm kiếm báo cáo tổng hợp toàn bộ
     * @param startDate Ngày bắt đầu (yyyy-MM-dd)
     * @param endDate Ngày kết thúc (yyyy-MM-dd)
     * @return Báo cáo tổng hợp toàn bộ
     */
    SummaryAllReportDTO searchSummaryAllReport(String startDate, String endDate);

    /**
     * Xuất báo cáo tổng hợp theo từng tổ chức
     * @param startDate Ngày bắt đầu (yyyy-MM-dd)
     * @param endDate Ngày kết thúc (yyyy-MM-dd)
     * @return Resource chứa file Excel
     */
    Resource exportSummaryByOrgReport(String startDate, String endDate);

    /**
     * Xuất báo cáo tổng hợp toàn bộ
     * @param startDate Ngày bắt đầu (yyyy-MM-dd)
     * @param endDate Ngày kết thúc (yyyy-MM-dd)
     * @return Resource chứa file Excel
     */
    Resource exportSummaryAllReport(String startDate, String endDate);
}
