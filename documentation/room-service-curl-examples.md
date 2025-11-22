# Danh sách cURL tích hợp API Dịch vụ phòng

## Cấu hình

```bash
# Base URL (thay đổi theo môi trường)
BASE_URL="http://localhost:8080/api/public"
TOKEN="your-jwt-token-here"
```

---

## 1. Tạo dịch vụ phòng

### 1.1. Tạo dịch vụ Điện
```bash
curl -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "ELECTRICITY",
    "price": 500000.00,
    "status": 1
  }'
```

### 1.2. Tạo dịch vụ Nước
```bash
curl -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "WATER",
    "price": 300000.00,
    "status": 1
  }'
```

### 1.3. Tạo dịch vụ Mạng
```bash
curl -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "INTERNET",
    "price": 400000.00,
    "status": 1
  }'
```

### 1.4. Tạo dịch vụ Tiền phòng
```bash
curl -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "ROOM_RENT",
    "price": 2000000.00,
    "status": 1
  }'
```

### 1.5. Tạo dịch vụ tùy chỉnh (OTHER)
```bash
curl -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "orgUnitId": "org-unit-id-123",
    "serviceType": "OTHER",
    "serviceCode": "CLEANING_SERVICE",
    "serviceName": "Dịch vụ vệ sinh",
    "price": 200000.00,
    "status": 1
  }'
```

---

## 2. Cập nhật dịch vụ phòng

### 2.1. Cập nhật giá dịch vụ
```bash
SERVICE_ID="123e4567-e89b-12d3-a456-426614174000"

curl -X PUT "${BASE_URL}/room-services/${SERVICE_ID}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "price": 600000.00
  }'
```

### 2.2. Cập nhật giá và trạng thái
```bash
SERVICE_ID="123e4567-e89b-12d3-a456-426614174000"

curl -X PUT "${BASE_URL}/room-services/${SERVICE_ID}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "price": 550000.00,
    "status": 1
  }'
```

### 2.3. Cập nhật dịch vụ OTHER (mã và tên)
```bash
SERVICE_ID="123e4567-e89b-12d3-a456-426614174000"

curl -X PUT "${BASE_URL}/room-services/${SERVICE_ID}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "serviceCode": "CLEANING_SERVICE_V2",
    "serviceName": "Dịch vụ vệ sinh nâng cao",
    "price": 250000.00
  }'
```

---

## 3. Xóa dịch vụ phòng

```bash
SERVICE_ID="123e4567-e89b-12d3-a456-426614174000"

curl -X DELETE "${BASE_URL}/room-services/${SERVICE_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 4. Lấy danh sách dịch vụ phòng

### 4.1. Lấy tất cả dịch vụ
```bash
curl -X GET "${BASE_URL}/room-services" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4.2. Lọc theo orgUnitId
```bash
ORG_UNIT_ID="org-unit-id-123"

curl -X GET "${BASE_URL}/room-services?orgUnitId=${ORG_UNIT_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4.3. Lọc theo serviceType
```bash
curl -X GET "${BASE_URL}/room-services?serviceType=ELECTRICITY" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4.4. Lọc theo status
```bash
curl -X GET "${BASE_URL}/room-services?status=1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4.5. Lọc kết hợp nhiều điều kiện
```bash
ORG_UNIT_ID="org-unit-id-123"

curl -X GET "${BASE_URL}/room-services?orgUnitId=${ORG_UNIT_ID}&serviceType=ELECTRICITY&status=1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 5. Lấy chi tiết dịch vụ phòng

```bash
SERVICE_ID="123e4567-e89b-12d3-a456-426614174000"

curl -X GET "${BASE_URL}/room-services/${SERVICE_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 6. Script tích hợp đầy đủ

```bash
#!/bin/bash

# Cấu hình
BASE_URL="http://localhost:8080/api/public"
TOKEN="your-jwt-token-here"
ORG_UNIT_ID="org-unit-id-123"

echo "=== Tạo các dịch vụ phòng cơ bản ==="

# Tạo dịch vụ Điện
echo "1. Tạo dịch vụ Điện..."
ELECTRICITY_RESPONSE=$(curl -s -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d "{
    \"orgUnitId\": \"${ORG_UNIT_ID}\",
    \"serviceType\": \"ELECTRICITY\",
    \"price\": 500000.00,
    \"status\": 1
  }")
echo "$ELECTRICITY_RESPONSE" | jq '.'
ELECTRICITY_ID=$(echo "$ELECTRICITY_RESPONSE" | jq -r '.id')
echo "ID dịch vụ Điện: $ELECTRICITY_ID"
echo ""

# Tạo dịch vụ Nước
echo "2. Tạo dịch vụ Nước..."
WATER_RESPONSE=$(curl -s -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d "{
    \"orgUnitId\": \"${ORG_UNIT_ID}\",
    \"serviceType\": \"WATER\",
    \"price\": 300000.00,
    \"status\": 1
  }")
echo "$WATER_RESPONSE" | jq '.'
echo ""

# Tạo dịch vụ Mạng
echo "3. Tạo dịch vụ Mạng..."
INTERNET_RESPONSE=$(curl -s -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d "{
    \"orgUnitId\": \"${ORG_UNIT_ID}\",
    \"serviceType\": \"INTERNET\",
    \"price\": 400000.00,
    \"status\": 1
  }")
echo "$INTERNET_RESPONSE" | jq '.'
echo ""

# Tạo dịch vụ Tiền phòng
echo "4. Tạo dịch vụ Tiền phòng..."
ROOM_RENT_RESPONSE=$(curl -s -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d "{
    \"orgUnitId\": \"${ORG_UNIT_ID}\",
    \"serviceType\": \"ROOM_RENT\",
    \"price\": 2000000.00,
    \"status\": 1
  }")
echo "$ROOM_RENT_RESPONSE" | jq '.'
echo ""

# Tạo dịch vụ tùy chỉnh
echo "5. Tạo dịch vụ tùy chỉnh (OTHER)..."
OTHER_RESPONSE=$(curl -s -X POST "${BASE_URL}/room-services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d "{
    \"orgUnitId\": \"${ORG_UNIT_ID}\",
    \"serviceType\": \"OTHER\",
    \"serviceCode\": \"CLEANING_SERVICE\",
    \"serviceName\": \"Dịch vụ vệ sinh\",
    \"price\": 200000.00,
    \"status\": 1
  }")
echo "$OTHER_RESPONSE" | jq '.'
echo ""

echo "=== Lấy danh sách dịch vụ ==="
curl -s -X GET "${BASE_URL}/room-services?orgUnitId=${ORG_UNIT_ID}" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.'

echo ""
echo "=== Cập nhật giá dịch vụ Điện ==="
if [ ! -z "$ELECTRICITY_ID" ] && [ "$ELECTRICITY_ID" != "null" ]; then
  curl -s -X PUT "${BASE_URL}/room-services/${ELECTRICITY_ID}" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
      "price": 550000.00
    }' | jq '.'
fi

echo ""
echo "=== Lấy chi tiết dịch vụ Điện ==="
if [ ! -z "$ELECTRICITY_ID" ] && [ "$ELECTRICITY_ID" != "null" ]; then
  curl -s -X GET "${BASE_URL}/room-services/${ELECTRICITY_ID}" \
    -H "Authorization: Bearer ${TOKEN}" | jq '.'
fi
```

---

## 7. Ví dụ với Postman Collection

### 7.1. Tạo dịch vụ Điện
```json
POST {{baseUrl}}/room-services
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "orgUnitId": "org-unit-id-123",
  "serviceType": "ELECTRICITY",
  "price": 500000.00,
  "status": 1
}
```

### 7.2. Lấy danh sách
```
GET {{baseUrl}}/room-services?orgUnitId=org-unit-id-123
Authorization: Bearer {{token}}
```

---

## 8. Lưu ý

1. **Token**: Thay `your-jwt-token-here` bằng JWT token thực tế
2. **Base URL**: Thay đổi theo môi trường (dev, staging, production)
3. **Service ID**: Lấy từ response khi tạo dịch vụ
4. **Org Unit ID**: Phải là ID hợp lệ của organization unit
5. **Service Type**: Chỉ chấp nhận: `ELECTRICITY`, `WATER`, `INTERNET`, `OTHER`, `ROOM_RENT`
6. **Status**: `1` = Hoạt động, `0` = Không hoạt động

---

## 9. Kiểm tra kết quả

### Format JSON response thành công:
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

### Lỗi thường gặp:
- `400 Bad Request`: Dữ liệu không hợp lệ
- `404 Not Found`: Dịch vụ hoặc organization unit không tồn tại
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn

