# 📋 Hướng Dẫn Sử Dụng Dữ Liệu Test Cho Trang Gia Hạn Phòng

## 📂 Các File Liên Quan

- **`GiaHanPhong_CreateRooms.sql`**: Script tạo phòng test (chạy trước)
- **`GiaHanPhong_TestData.sql`**: Script tạo dữ liệu test
- **`GiaHanPhong_CleanTestData.sql`**: Script xóa dữ liệu test
- **`GiaHanPhong_CheckData.sql`**: Script kiểm tra dữ liệu
- **`GiaHanPhong_TestData_README.md`**: File hướng dẫn này

---

## 🎯 Mục Đích

Tạo dữ liệu test để kiểm thử chức năng **Gia Hạn Phòng** trong hệ thống quản lý khách sạn Victorya.

---

## 🚀 Cách Sử Dụng

### Bước 1️⃣: Tạo Phòng Test (Nếu Chưa Có)

```sql
-- Chạy file này trong SQL Server Management Studio
-- File: GiaHanPhong_CreateRooms.sql
```

**Kết quả mong đợi:**
```
✅ Đã tạo loại phòng Standard (500,000 VND)
✅ Đã tạo loại phòng VIP (1,000,000 VND)
✅ Đã tạo phòng P401 (Tầng 4 - Standard)
✅ Đã tạo phòng P402 (Tầng 4 - VIP)
✅ Đã tạo phòng P403 (Tầng 4 - VIP)
✅ Đã tạo loại đặt phòng "Theo ngày"
```

### Bước 2️⃣: Tạo Dữ Liệu Test

```sql
-- File: GiaHanPhong_TestData.sql
```

**Kết quả mong đợi:**
```
✅ Đã tạo khách hàng test 1: Phạm Văn Đức (CCCD: 999999999999)
✅ Đã tạo khách hàng test 2: Võ Thị Em (CCCD: 888888888888)
✅ Đã cập nhật 3 phòng về trạng thái "Đang ở"
✅ Đã tạo phiếu đặt phòng: PDP_GIAHAN001
✅ Đã tạo phiếu đặt phòng: PDP_GIAHAN002
✅ Đã tạo chi tiết P401 - Còn 3 giờ (CẦN GIA HẠN GẤP)
✅ Đã tạo chi tiết P402 - Còn 1 ngày
✅ Đã tạo chi tiết P403 - Còn 2 ngày
```

### Bước 3️⃣: Test Trong Ứng Dụng

1. Mở trang **Gia Hạn Phòng**
2. Nhập CCCD test
3. Click **Tìm kiếm**
4. Chọn phòng từ danh sách
5. Chọn thời gian gia hạn mới
6. Click **Gia hạn phòng**

### Bước 4️⃣: Kiểm Tra Dữ Liệu (Tùy Chọn)

```sql
-- File: GiaHanPhong_CheckData.sql
-- Xem dữ liệu hiện tại
```

### Bước 5️⃣: Xóa Dữ Liệu Test (Sau Khi Test Xong)

```sql
-- File: GiaHanPhong_CleanTestData.sql
```

---

## 📊 Chi Tiết Dữ Liệu Test

### Khách Hàng 1: **Phạm Văn Đức**
- **CCCD**: `999999999999`
- **SĐT**: 0909876543
- **Email**: phamvanduc@gmail.com
- **Phòng đang ở**:
  - **P401** (Tầng 4, Standard)
    - Đã ở: 2 ngày
    - Còn lại: **3 giờ** ⚠️ (cần gia hạn gấp!)
    - Giá: 500,000 VND/ngày

### Khách Hàng 2: **Võ Thị Em**
- **CCCD**: `888888888888`
- **SĐT**: 0918765432
- **Email**: vothiem@gmail.com
- **Phòng đang ở**:
  - **P402** (Tầng 4, VIP)
    - Đã ở: 1 ngày
    - Còn lại: **1 ngày**
    - Giá: 1,000,000 VND/ngày
  - **P403** (Tầng 4, VIP)
    - Đã ở: 1 ngày
    - Còn lại: **2 ngày**
    - Giá: 1,000,000 VND/ngày

---

## ✅ Các Test Case

### Test Case 1: **Gia Hạn 1 Phòng Gấp**
**CCCD**: `999999999999`

**Kịch bản:**
- Phòng P401 còn 3 giờ → Cần gia hạn ngay
- Chọn gia hạn thêm 2 ngày

**Kết quả mong đợi:**
- ✅ Hiển thị thông tin: Phạm Văn Đức
- ✅ Hiển thị 1 phòng: P401, còn "3 giờ"
- ✅ Form gia hạn inline hiện ngay
- ✅ Gia hạn thành công
- ✅ Thời gian trả mới = Hiện tại + 2 ngày

### Test Case 2: **Gia Hạn Nhiều Phòng Cùng Lúc**
**CCCD**: `888888888888`

**Kịch bản:**
- Khách có 2 phòng VIP
- Chọn cả 2 phòng P402, P403
- Gia hạn P402 thêm 1 ngày, P403 thêm 3 ngày

**Kết quả mong đợi:**
- ✅ Hiển thị thông tin: Võ Thị Em
- ✅ Hiển thị 2 phòng với thời gian còn lại
- ✅ 2 form gia hạn hiển thị inline
- ✅ Có thể chọn thời gian khác nhau cho mỗi phòng
- ✅ Gia hạn 2 phòng thành công

### Test Case 3: **Validation Thời Gian**
**CCCD**: `999999999999`

**Kịch bản:**
- Chọn phòng P401
- Chọn thời gian gia hạn < thời gian trả hiện tại (VD: ngày hôm qua)

**Kết quả mong đợi:**
- ❌ Thông báo: "Thời gian không hợp lệ"
- ❌ Gia hạn thất bại

### Test Case 4: **Cột "Thời Gian Còn Lại"**
**CCCD**: `999999999999` hoặc `888888888888`

**Kết quả mong đợi:**
- ✅ P401: Hiển thị "3 giờ" (màu đỏ nếu < 12h)
- ✅ P402: Hiển thị "1 ngày"
- ✅ P403: Hiển thị "2 ngày"

### Test Case 5: **CCCD Không Tồn Tại**
**CCCD**: `000000000000`

**Kết quả mong đợi:**
- ✅ Thông báo: "Không tìm thấy phòng đang ở"
- ✅ Bảng danh sách trống

---

## 🔍 SQL Queries Kiểm Tra

### Kiểm tra phòng đang ở:
```sql
SELECT 
    kh.CCCD,
    kh.hoTen,
    p.soPhong,
    p.trangThai,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong,
    DATEDIFF(HOUR, GETDATE(), ctpdp.thoiGianTraPhong) AS SoGioConLai
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
JOIN Phong p ON ctpdp.maPhong = p.maPhong
WHERE kh.CCCD IN ('999999999999', '888888888888')
    AND p.trangThai = N'Đang ở'
ORDER BY kh.CCCD, p.soPhong;
```

### Kiểm tra sau khi gia hạn:
```sql
SELECT 
    p.maPhong,
    p.soPhong,
    ctpdp.thoiGianTraPhong AS ThoiGianTraMoi,
    DATEDIFF(DAY, GETDATE(), ctpdp.thoiGianTraPhong) AS SoNgayConLai
FROM ChiTietPhieuDatPhong ctpdp
JOIN Phong p ON ctpdp.maPhong = p.maPhong
WHERE p.maPhong IN ('P401', 'P402', 'P403')
ORDER BY p.maPhong;
```

---

## 🎨 Trạng Thái Phòng

| Trạng thái | Mô tả | Hiển thị trong Gia Hạn |
|------------|-------|-------------------------|
| **Sẵn sàng** | Phòng trống | ❌ Không |
| **Đã đặt** | Đã đặt, chưa nhận | ❌ Không |
| **Đang ở** | Khách đang ở | ✅ Có |
| **Đang dọn** | Đang dọn dẹp | ❌ Không |

---

## ⚠️ Lưu Ý

1. **Thời gian động**: Dữ liệu test sử dụng GETDATE(), luôn phù hợp thời điểm chạy
2. **Có thể chạy lại**: Script kiểm tra và cập nhật nếu dữ liệu đã tồn tại
3. **Phòng riêng biệt**: Dùng phòng Tầng 4 (P401-P403) để không ảnh hưởng test khác
4. **Thời gian còn lại**: 
   - P401: 3 giờ (ưu tiên gia hạn)
   - P402: 1 ngày
   - P403: 2 ngày

---

## 🔄 Luồng Nghiệp Vụ

```
1. Khách nhận phòng
   ↓
2. Trạng thái: "Đang ở"
   ↓
3. Thời gian sắp hết
   ↓
4. Nhân viên gia hạn phòng cho khách
   ↓
5. Cập nhật thoiGianTraPhong mới
   ↓
6. Khách tiếp tục ở
```

---

## 📁 Thứ Tự Chạy Script

| File | Mục Đích | Thứ Tự |
|------|----------|---------|
| `GiaHanPhong_CreateRooms.sql` | Tạo phòng test | 1️⃣ (bắt buộc) |
| `GiaHanPhong_TestData.sql` | Tạo khách & phiếu đặt | 2️⃣ |
| `GiaHanPhong_CheckData.sql` | Kiểm tra dữ liệu | 3️⃣ (tùy chọn) |
| `GiaHanPhong_CleanTestData.sql` | Xóa dữ liệu test | 4️⃣ (sau test) |

---

## 🎯 Đặc Điểm Nổi Bật

### 1. Cột "Thời Gian Còn Lại" ⏰
Hiển thị trực quan thời gian còn lại:
- `"3 giờ"` - Còn ít, cần gia hạn gấp
- `"1 ngày"` - Vừa phải
- `"2 ngày"` - Còn nhiều

### 2. Form Gia Hạn Inline 🎯
- Không cần modal popup
- Tất cả form hiển thị ngay
- Chọn thời gian cho nhiều phòng cùng lúc

### 3. Thông Tin Khách Hàng 📋
- Card đẹp hiển thị đầy đủ thông tin
- Số phòng đang ở
- Dễ xác nhận khách hàng

---

## 💡 Tips Test

### Test Nhanh
```sql
-- Chỉ cần chạy 2 script:
1. GiaHanPhong_CreateRooms.sql   (1 lần duy nhất)
2. GiaHanPhong_TestData.sql       (mỗi lần test)
```

### Test Lại Nhiều Lần
```sql
-- Cứ chạy lại GiaHanPhong_TestData.sql
-- Thời gian sẽ tự động cập nhật về hiện tại
```

### Xóa Dữ Liệu
```sql
-- Sau khi test xong, làm sạch database
-- File: GiaHanPhong_CleanTestData.sql
```

---

## 📞 Hỗ Trợ

### Nếu Gặp Lỗi Foreign Key:
```
1. Chạy GiaHanPhong_CreateRooms.sql trước
2. Sau đó chạy GiaHanPhong_TestData.sql
```

### Nếu Không Thấy Phòng Trong App:
```
1. Chạy GiaHanPhong_CheckData.sql
2. Kiểm tra trạng thái phòng = "Đang ở"
3. Kiểm tra GETDATE() BETWEEN thoiGianNhanPhong AND thoiGianTraPhong
```

---

## 🎨 So Sánh Với Test Nhận Phòng

| Tính năng | Nhận Phòng | Gia Hạn Phòng |
|-----------|------------|---------------|
| **CCCD test** | 123456789012, 987654321098 | 999999999999, 888888888888 |
| **Phòng test** | P101-P103, P201-P202, P301 | P401-P403 |
| **Tầng** | Tầng 1-3 | Tầng 4 |
| **Trạng thái** | "Đã đặt" | "Đang ở" |
| **Tính năng đặc biệt** | Thời gian nhận | Thời gian còn lại ⏰ |
| **Panel phải** | Label tóm tắt | Form gia hạn inline |

---

## ✨ Tính Năng Test Được

- ✅ Tìm phòng đang ở theo CCCD
- ✅ Hiển thị thông tin khách hàng
- ✅ Hiển thị cột "Thời gian còn lại" ⏰
- ✅ Chọn một hoặc nhiều phòng
- ✅ Form gia hạn inline (không modal)
- ✅ Validation thời gian
- ✅ Gia hạn phòng (cập nhật database)
- ✅ Cập nhật tự động sau khi gia hạn

---

**Chúc bạn test thành công! 🎉**

---

## 📝 Changelog

**v1.0** - 27/10/2025
- Tạo dữ liệu test cho GiaHanPhong_GUI_New
- Phù hợp với schema Victorya_Hotel_v4.sql
- 2 khách hàng, 3 phòng tầng 4
- Thời gian động, có thể chạy lại

