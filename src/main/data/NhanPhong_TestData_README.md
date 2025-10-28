# 📋 Hướng Dẫn Sử Dụng Dữ Liệu Test Cho Trang Nhận Phòng

## 📂 Các File Liên Quan

- **`NhanPhong_TestData.sql`**: Script tạo dữ liệu test
- **`NhanPhong_CleanTestData.sql`**: Script xóa dữ liệu test
- **`NhanPhong_TestData_README.md`**: File hướng dẫn này

---

## 🎯 Mục Đích

Tạo dữ liệu test để kiểm thử chức năng **Nhận Phòng** trong hệ thống quản lý khách sạn Victorya.

---

## 🚀 Cách Sử Dụng

### 1️⃣ Tạo Dữ Liệu Test

```sql
-- Chạy file này trong SQL Server Management Studio hoặc Azure Data Studio
-- File: NhanPhong_TestData.sql
```

**Các bước:**
1. Mở **SQL Server Management Studio** hoặc **Azure Data Studio**
2. Kết nối đến database `Victorya_Hotel`
3. Mở file `NhanPhong_TestData.sql`
4. Click **Execute** (F5) để chạy script

### 2️⃣ Test Chức Năng Nhận Phòng

Sau khi chạy script, bạn có 3 khách hàng để test:

| CCCD | Tên Khách Hàng | Số Phòng Chờ Nhận | Mô Tả |
|------|----------------|-------------------|-------|
| `123456789012` | Nguyễn Văn An | 1 phòng | Test case đơn giản - 1 phòng |
| `987654321098` | Trần Thị Bình | 3 phòng | Test case phức tạp - nhiều phòng |
| `456789123456` | Lê Hoàng Cường | 1 phòng chờ + 1 đang ở | Test phân biệt trạng thái |

**Bước test trong ứng dụng:**

1. Mở trang **Nhận Phòng** trong ứng dụng
2. Nhập một trong các CCCD ở trên
3. Click **Tìm kiếm**
4. Chọn phòng từ danh sách
5. Click **Nhận phòng**

### 3️⃣ Xóa Dữ Liệu Test (Khi Hoàn Thành)

```sql
-- Chạy file này để xóa tất cả dữ liệu test
-- File: NhanPhong_CleanTestData.sql
```

---

## 📊 Chi Tiết Dữ Liệu Test

### Khách Hàng 1: **Nguyễn Văn An**
- **CCCD**: `123456789012`
- **SĐT**: 0901234567
- **Email**: nguyenvanan@gmail.com
- **Phòng chờ nhận**:
  - P101 (Tầng 1) - Đã đến giờ nhận từ 2 giờ trước

### Khách Hàng 2: **Trần Thị Bình**
- **CCCD**: `987654321098`
- **SĐT**: 0912345678
- **Email**: tranthibinh@gmail.com
- **Phòng chờ nhận**:
  - P102 (Tầng 1) - Đã đến giờ nhận từ 1 giờ trước
  - P201 (Tầng 2) - Đã đến giờ nhận từ 30 phút trước
  - P202 (Tầng 2) - Đúng giờ nhận bây giờ

### Khách Hàng 3: **Lê Hoàng Cường**
- **CCCD**: `456789123456`
- **SĐT**: 0923456789
- **Email**: lehoangcuong@gmail.com
- **Phòng chờ nhận**:
  - P301 (Tầng 3) - Đã đến giờ nhận từ 3 giờ trước
- **Phòng đang ở** (không hiển thị trong danh sách nhận phòng):
  - P103 (Tầng 1) - Đã nhận từ 1 ngày trước

---

## ✅ Các Test Case

### Test Case 1: Nhận 1 Phòng Đơn Giản
**CCCD**: `123456789012`

**Kết quả mong đợi:**
- ✅ Hiển thị thông tin khách hàng: Nguyễn Văn An
- ✅ Hiển thị 1 phòng trong danh sách: P101
- ✅ Có thể chọn và nhận phòng
- ✅ Sau khi nhận, trạng thái phòng chuyển từ "Đã đặt" → "Đang ở"

### Test Case 2: Nhận Nhiều Phòng Cùng Lúc
**CCCD**: `987654321098`

**Kết quả mong đợi:**
- ✅ Hiển thị thông tin khách hàng: Trần Thị Bình
- ✅ Hiển thị 3 phòng: P102, P201, P202
- ✅ Có thể chọn nhiều phòng cùng lúc
- ✅ Nhận nhiều phòng thành công
- ✅ Tất cả phòng được chọn chuyển sang "Đang ở"

### Test Case 3: Phân Biệt Phòng Chờ Nhận vs Đang Ở
**CCCD**: `456789123456`

**Kết quả mong đợi:**
- ✅ Hiển thị thông tin khách hàng: Lê Hoàng Cường
- ✅ Chỉ hiển thị P301 (phòng chờ nhận)
- ✅ KHÔNG hiển thị P103 (phòng đang ở)
- ✅ Nhận phòng P301 thành công

### Test Case 4: CCCD Không Tồn Tại
**CCCD**: `111111111111`

**Kết quả mong đợi:**
- ✅ Hiển thị thông báo: "Không tìm thấy phòng chờ nhận cho CCCD này"
- ✅ Không hiển thị thông tin khách hàng
- ✅ Bảng danh sách phòng trống

---

## 🔍 Kiểm Tra Dữ Liệu

### Kiểm tra phòng chờ nhận:
```sql
SELECT 
    kh.CCCD,
    kh.hoTen,
    p.soPhong,
    p.trangThai,
    ctpdp.thoiGianNhanPhong,
    ctpdp.thoiGianTraPhong
FROM KhachHang kh
JOIN PhieuDatPhong pdp ON kh.maKhachHang = pdp.maKhachHang
JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong
JOIN Phong p ON ctpdp.maPhong = p.maPhong
WHERE kh.CCCD IN ('123456789012', '987654321098', '456789123456')
    AND p.trangThai = N'Đã đặt'
ORDER BY kh.CCCD, p.soPhong;
```

### Kiểm tra phòng sau khi nhận:
```sql
SELECT 
    p.maPhong,
    p.soPhong,
    p.trangThai,
    p.tang
FROM Phong p
WHERE p.maPhong IN ('P101', 'P102', 'P201', 'P202', 'P301')
ORDER BY p.tang, p.maPhong;
```

---

## 🎨 Trạng Thái Phòng

| Trạng thái | Mô tả | Hiển thị trong Nhận Phòng |
|------------|-------|---------------------------|
| **Sẵn sàng** | Phòng trống, chưa đặt | ❌ Không |
| **Đã đặt** | Đã đặt, chưa nhận | ✅ Có (nếu đến giờ) |
| **Đang ở** | Khách đang ở | ❌ Không |
| **Đang dọn** | Đang dọn dẹp | ❌ Không |

---

## ⚠️ Lưu Ý

1. **Thời gian**: Dữ liệu test sử dụng thời gian động (GETDATE()), vì vậy luôn phù hợp với thời điểm chạy script
2. **An toàn**: Script kiểm tra dữ liệu đã tồn tại trước khi INSERT để tránh lỗi duplicate
3. **Cập nhật**: Nếu chạy lại script, dữ liệu sẽ được cập nhật thời gian mới
4. **Phòng**: Script tự động cập nhật trạng thái 6 phòng (P101-P103, P201-P202, P301)

---

## 🔄 Luồng Nghiệp Vụ

```
1. Khách đặt phòng
   ↓
2. Trạng thái: "Đã đặt"
   ↓
3. Đến giờ nhận (thoiGianNhanPhong <= GETDATE())
   ↓
4. Nhân viên nhận phòng cho khách
   ↓
5. Trạng thái: "Đang ở"
   ↓
6. Khách trả phòng
   ↓
7. Trạng thái: "Sẵn sàng"
```

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề khi chạy script:

1. Kiểm tra kết nối database
2. Đảm bảo database `Victorya_Hotel` đã tồn tại
3. Kiểm tra quyền thực thi (INSERT, UPDATE, DELETE)
4. Xem log output để biết chi tiết lỗi

---

## ✨ Tính Năng Test Được

- ✅ Tìm kiếm phòng chờ nhận theo CCCD
- ✅ Hiển thị thông tin khách hàng
- ✅ Hiển thị danh sách phòng chờ nhận
- ✅ Chọn một hoặc nhiều phòng
- ✅ Nhận phòng (cập nhật trạng thái)
- ✅ Phân biệt phòng "Đã đặt" và "Đang ở"
- ✅ Kiểm tra thời gian hợp lệ
- ✅ Xử lý trường hợp không tìm thấy

---

**Chúc bạn test thành công! 🎉**

