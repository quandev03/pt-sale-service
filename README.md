# customer-service

## Prerequisites

### Java

You need to have Java 17:
- [JDK 17](https://openjdk.java.net/projects/jdk/17/)

## Local environment

- [Local server](http://localhost:8080)
- [Local API doc](http://localhost:8080/swagger-ui/index.html)

## Documentation

- [Hexagonal architecture](documentation/hexagonal-architecture.md)
- [Package types](documentation/package-types.md)
- [Assertions](documentation/assertions.md)
- [MySQL](documentation/mysql.md)
- [Redis](documentation/redis.md)
- [Apache Kafka](documentation/apache-kafka.md)
- [Caffeine](documentation/caffeine.md)
- [Application errors](documentation/application-errors.md)
- [Jpa pages](documentation/jpa-pages.md)
- [Logs Spy](documentation/logs-spy.md)
- [CORS configuration](documentation/cors-configuration.md)
- [Rest pagination](documentation/rest-pagination.md)

## Partner package subscriptions

|Endpoint|Description|
|---|---|
|`POST ${application.path.base.private}/partner-package-subscriptions`|Tạo gói dịch vụ cho đối tác. Đầu vào gồm `organizationUnitId`, `packageProfileId`, `startTime` (tùy chọn). Hệ thống kiểm tra trạng thái hoạt động của đơn vị/gói, tính `endTime = startTime + cycleValue * cycleUnit`.|
|`GET ${application.path.base.private}/partner-package-subscriptions`|Danh sách gói đã bán với các tham số lọc `organizationUnitId`, `packageProfileId`, `status` và hỗ trợ phân trang. Kết quả gồm tên đối tác, tên gói, thời gian bắt đầu/kết thúc, trạng thái.|
|`POST ${application.path.base.private}/partner-package-subscriptions/{id}/stop`|Dừng thủ công một gói đang hoạt động, đồng thời cập nhật `endTime` = thời điểm hiện tại.|

### Automatic expiration job

- `PartnerPackageSubscriptionScheduler` chạy mỗi 30 phút để tự động chuyển trạng thái `ACTIVE` sang `EXPIRED` nếu `endTime` đã đến hạn.

<!-- jhipster-needle-documentation -->
