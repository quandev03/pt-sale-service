# API Tài liệu tích hợp - Dịch vụ phòng (Room Service)

## Tổng quan

API quản lý dịch vụ phòng cho phép thêm, sửa, xóa và tra cứu các dịch vụ phòng. Mỗi dịch vụ gắn với một đơn vị tổ chức (phòng) và có mã dịch vụ unique theo clientID.

## Base URL

```
{application.path.base.public}/room-services
```

## Loại dịch vụ (Service Type)

- `ELECTRICITY` - Điện (mã tự động: "ELECTRICITY")
- `WATER` - Nước (mã tự động: "WATER")
- `INTERNET` - Mạng (mã tự động: "INTERNET")
- `ROOM_RENT` - Tiền phòng (mã tự động: "ROOM_RENT")
- `OTHER` - Khác (mã và tên do user nhập)

## Quy tắc nghiệp vụ

1. Mã dịch vụ (serviceCode) unique theo clientID
2. Loại dịch vụ ELECTRICITY, WATER, INTERNET, ROOM_RENT: serviceCode và serviceName tự động từ enum
3. Loại OTHER: serviceCode và serviceName bắt buộc do user nhập
4. Giá dịch vụ (price) bắt buộc, phải lớn hơn 0

---

## 1. Tạo dịch vụ phòng

**Endpoint:** `POST /room-services`

**Request Body:**

```json
{
  "orgUnitId": "org-unit-id-123",
  "serviceType": "ELECTRICITY",
  "price": 500000.00,
  "status": 1
}
```

**Lưu ý:**
- Với `ELECTRICITY`, `WATER`, `INTERNET`, `ROOM_RENT`: không cần gửi `serviceCode` và `serviceName` (tự động)
- Với `OTHER`: bắt buộc gửi `serviceCode` và `serviceName`

**Ví dụ với loại OTHER:**

```json
{
  "orgUnitId": "org-unit-id-123",
  "serviceType": "OTHER",
  "serviceCode": "CUSTOM_SERVICE_001",
  "serviceName": "Dịch vụ tùy chỉnh",
  "price": 300000.00,
  "status": 1
}
```

**cURL:**

```bash
curl -X POST "http://localhost:8080/api/public/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "ELECTRICITY",
    "price": 500000.00,
    "status": 1
  }'
```

**Response 200:**

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orgUnitId": "org-unit-id-123",
  "serviceType": "ELECTRICITY",
  "serviceCode": "ELECTRICITY",
  "serviceName": "Điện",
  "price": 500000.00,
  "clientId": "client-id-123",
  "status": 1,
  "createdBy": "admin",
  "createdDate": "2024-11-23T10:00:00",
  "modifiedBy": null,
  "modifiedDate": null
}
```

---

## 2. Cập nhật dịch vụ phòng

**Endpoint:** `PUT /room-services/{id}`

**Request Body:**

```json
{
  "price": 600000.00,
  "status": 1
}
```

**cURL:**

```bash
curl -X PUT "http://localhost:8080/api/public/room-services/123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "price": 600000.00,
    "status": 1
  }'
```

**Response 200:**

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orgUnitId": "org-unit-id-123",
  "serviceType": "ELECTRICITY",
  "serviceCode": "ELECTRICITY",
  "serviceName": "Điện",
  "price": 600000.00,
  "clientId": "client-id-123",
  "status": 1,
  "createdBy": "admin",
  "createdDate": "2024-11-23T10:00:00",
  "modifiedBy": "admin",
  "modifiedDate": "2024-11-23T11:00:00"
}
```

---

## 3. Xóa dịch vụ phòng

**Endpoint:** `DELETE /room-services/{id}`

**cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/public/room-services/123e4567-e89b-12d3-a456-426614174000" \
  -H "Authorization: Bearer {token}"
```

**Response 200:**

```json
{}
```

---

## 4. Lấy danh sách dịch vụ phòng

**Endpoint:** `GET /room-services`

**Query Parameters:**
- `orgUnitId` (optional): Lọc theo mã đơn vị tổ chức
- `serviceType` (optional): Lọc theo loại dịch vụ (ELECTRICITY, WATER, INTERNET, OTHER, ROOM_RENT)
- `status` (optional): Lọc theo trạng thái (1: Hoạt động, 0: Không hoạt động)

**cURL:**

```bash
# Lấy tất cả
curl -X GET "http://localhost:8080/api/public/room-services" \
  -H "Authorization: Bearer {token}"

# Lọc theo orgUnitId
curl -X GET "http://localhost:8080/api/public/room-services?orgUnitId=org-unit-id-123" \
  -H "Authorization: Bearer {token}"

# Lọc theo serviceType
curl -X GET "http://localhost:8080/api/public/room-services?serviceType=ELECTRICITY" \
  -H "Authorization: Bearer {token}"

# Lọc theo status
curl -X GET "http://localhost:8080/api/public/room-services?status=1" \
  -H "Authorization: Bearer {token}"

# Lọc kết hợp
curl -X GET "http://localhost:8080/api/public/room-services?orgUnitId=org-unit-id-123&serviceType=ELECTRICITY&status=1" \
  -H "Authorization: Bearer {token}"
```

**Response 200:**

```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "orgUnitId": "org-unit-id-123",
    "serviceType": "ELECTRICITY",
    "serviceCode": "ELECTRICITY",
    "serviceName": "Điện",
    "price": 500000.00,
    "clientId": "client-id-123",
    "status": 1,
    "createdBy": "admin",
    "createdDate": "2024-11-23T10:00:00",
    "modifiedBy": null,
    "modifiedDate": null
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "orgUnitId": "org-unit-id-123",
    "serviceType": "WATER",
    "serviceCode": "WATER",
    "serviceName": "Nước",
    "price": 300000.00,
    "clientId": "client-id-123",
    "status": 1,
    "createdBy": "admin",
    "createdDate": "2024-11-23T10:05:00",
    "modifiedBy": null,
    "modifiedDate": null
  }
]
```

---

## 5. Lấy chi tiết dịch vụ phòng

**Endpoint:** `GET /room-services/{id}`

**cURL:**

```bash
curl -X GET "http://localhost:8080/api/public/room-services/123e4567-e89b-12d3-a456-426614174000" \
  -H "Authorization: Bearer {token}"
```

**Response 200:**

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orgUnitId": "org-unit-id-123",
  "serviceType": "ELECTRICITY",
  "serviceCode": "ELECTRICITY",
  "serviceName": "Điện",
  "price": 500000.00,
  "clientId": "client-id-123",
  "status": 1,
  "createdBy": "admin",
  "createdDate": "2024-11-23T10:00:00",
  "modifiedBy": null,
  "modifiedDate": null
}
```

---

## Mã lỗi thường gặp

### 400 Bad Request
- Mã dịch vụ đã tồn tại cho đối tác này
- Loại dịch vụ không được để trống
- Mã dịch vụ không được để trống cho loại dịch vụ 'Khác'
- Tên dịch vụ không được để trống cho loại dịch vụ 'Khác'
- Giá dịch vụ phải lớn hơn 0

### 404 Not Found
- Dịch vụ phòng không tồn tại
- Đơn vị tổ chức không tồn tại

---

## Ví dụ tích hợp đầy đủ

### Tạo các dịch vụ phòng cơ bản

```bash
# 1. Tạo dịch vụ Điện
curl -X POST "http://localhost:8080/api/public/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "ELECTRICITY",
    "price": 500000.00,
    "status": 1
  }'

# 2. Tạo dịch vụ Nước
curl -X POST "http://localhost:8080/api/public/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "WATER",
    "price": 300000.00,
    "status": 1
  }'

# 3. Tạo dịch vụ Mạng
curl -X POST "http://localhost:8080/api/public/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "INTERNET",
    "price": 400000.00,
    "status": 1
  }'

# 4. Tạo dịch vụ Tiền phòng
curl -X POST "http://localhost:8080/api/public/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "ROOM_RENT",
    "price": 2000000.00,
    "status": 1
  }'

# 5. Tạo dịch vụ tùy chỉnh (OTHER)
curl -X POST "http://localhost:8080/api/public/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "OTHER",
    "serviceCode": "CLEANING_SERVICE",
    "serviceName": "Dịch vụ vệ sinh",
    "price": 200000.00,
    "status": 1
  }'
```

### Lấy danh sách và cập nhật

```bash
# Lấy tất cả dịch vụ của một phòng
curl -X GET "http://localhost:8080/api/public/room-services?orgUnitId=org-unit-id-123" \
  -H "Authorization: Bearer {token}"

# Cập nhật giá dịch vụ Điện
curl -X PUT "http://localhost:8080/api/public/room-services/{electricity-service-id}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "price": 550000.00
  }'
```

---

## Lưu ý

1. Tất cả các API đều yêu cầu authentication token trong header `Authorization: Bearer {token}`
2. Mã dịch vụ (serviceCode) phải unique trong cùng một clientID
3. Với loại dịch vụ `OTHER`, bắt buộc phải cung cấp `serviceCode` và `serviceName`
4. Với các loại dịch vụ khác (ELECTRICITY, WATER, INTERNET, ROOM_RENT), `serviceCode` và `serviceName` sẽ được tự động tạo
5. Giá dịch vụ (price) phải là số dương

