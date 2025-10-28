# 📊 Tổng Quan Dữ Liệu Test - Nhận Phòng & Gia Hạn Phòng

## 🎯 Mục Đích
Tài liệu tổng hợp tất cả dữ liệu test cho 2 chức năng quan trọng:
- **Nhận Phòng** (NhanPhong_GUI)
- **Gia Hạn Phòng** (GiaHanPhong_GUI_New)

---

## 📁 Cấu Trúc Files

### 🏨 Nhận Phòng
```
src/main/data/
├── NhanPhong_CreateRooms.sql      (Tạo phòng Tầng 1-3)
├── NhanPhong_TestData.sql          (Tạo dữ liệu test)
├── NhanPhong_CleanTestData.sql     (Xóa dữ liệu test)
├── NhanPhong_CheckData.sql         (Kiểm tra dữ liệu)
└── NhanPhong_TestData_README.md    (Hướng dẫn)
```

### ⏰ Gia Hạn Phòng
```
src/main/data/
├── GiaHanPhong_CreateRooms.sql     (Tạo phòng Tầng 4)
├── GiaHanPhong_TestData.sql        (Tạo dữ liệu test)
├── GiaHanPhong_CleanTestData.sql   (Xóa dữ liệu test)
├── GiaHanPhong_CheckData.sql       (Kiểm tra dữ liệu)
└── GiaHanPhong_TestData_README.md  (Hướng dẫn)
```

---

## 📊 So Sánh Dữ Liệu Test

### Khách Hàng Test

| Chức năng | CCCD | Tên | Số Phòng | Tầng |
|-----------|------|-----|----------|------|
| **Nhận Phòng** | 123456789012 | Nguyễn Văn An | 1 phòng | 1 |
| **Nhận Phòng** | 987654321098 | Trần Thị Bình | 3 phòng | 1-2 |
| **Nhận Phòng** | 456789123456 | Lê Hoàng Cường | 1 chờ + 1 đang ở | 1,3 |
| **Gia Hạn** | 999999999999 | Phạm Văn Đức | 1 phòng | 4 |
| **Gia Hạn** | 888888888888 | Võ Thị Em | 2 phòng | 4 |

### Phòng Test

| Phòng | Tầng | Loại | Trạng thái | Chức năng |
|-------|------|------|------------|-----------|
| **P101-P103** | 1 | Standard | Đã đặt | Nhận Phòng |
| **P201-P202** | 2 | VIP | Đã đặt | Nhận Phòng |
| **P301** | 3 | VIP | Đã đặt | Nhận Phòng |
| **P401** | 4 | Standard | Đang ở | Gia Hạn |
| **P402-P403** | 4 | VIP | Đang ở | Gia Hạn |

### Phân Chia Rõ Ràng
- ✅ Tầng 1-3: Test **Nhận Phòng** (trạng thái "Đã đặt")
- ✅ Tầng 4: Test **Gia Hạn** (trạng thái "Đang ở")
- ✅ Không trùng lặp, không xung đột

---

## 🚀 Hướng Dẫn Sử Dụng Tổng Hợp

### Setup Lần Đầu (1 lần duy nhất)

```sql
-- Bước 1: Tạo phòng cho Nhận Phòng
EXEC NhanPhong_CreateRooms.sql

-- Bước 2: Tạo phòng cho Gia Hạn
EXEC GiaHanPhong_CreateRooms.sql
```

### Test Nhận Phòng

```sql
-- Tạo dữ liệu
EXEC NhanPhong_TestData.sql

-- Test trong app với CCCD: 123456789012, 987654321098, 456789123456

-- Xóa sau khi test (tùy chọn)
EXEC NhanPhong_CleanTestData.sql
```

### Test Gia Hạn Phòng

```sql
-- Tạo dữ liệu
EXEC GiaHanPhong_TestData.sql

-- Test trong app với CCCD: 999999999999, 888888888888

-- Xóa sau khi test (tùy chọn)
EXEC GiaHanPhong_CleanTestData.sql
```

---

## 🔄 Workflow Hoàn Chỉnh

```
1. ĐĂNG KÝ PHÒNG
   ↓ (Đặt phòng)
   
2. PHÒNG "ĐÃ ĐẶT" 
   ↓ (Nhận phòng) ← Test với NhanPhong_TestData.sql
   
3. PHÒNG "ĐANG Ở"
   ↓ (Gia hạn nếu cần) ← Test với GiaHanPhong_TestData.sql
   
4. TRẢ PHÒNG
   ↓ (Thanh toán)
   
5. PHÒNG "SẴN SÀNG"
```

---

## 📋 Checklist Test

### Nhận Phòng ✅
- [ ] Tìm 1 phòng (CCCD: 123456789012)
- [ ] Tìm nhiều phòng (CCCD: 987654321098)
- [ ] Phân biệt "Đã đặt" vs "Đang ở" (CCCD: 456789123456)
- [ ] CCCD không tồn tại
- [ ] Nhận 1 phòng
- [ ] Nhận nhiều phòng cùng lúc
- [ ] Cập nhật trạng thái → "Đang ở"

### Gia Hạn Phòng ✅
- [ ] Tìm 1 phòng sắp hết hạn (CCCD: 999999999999)
- [ ] Tìm nhiều phòng (CCCD: 888888888888)
- [ ] Hiển thị cột "Thời gian còn lại"
- [ ] Form gia hạn inline
- [ ] Chọn thời gian cho nhiều phòng
- [ ] Validation thời gian
- [ ] Gia hạn thành công
- [ ] Cập nhật thoiGianTraPhong

---

## 🎨 Database Schema (Tham Khảo)

### ChiTietPhieuDatPhong
```sql
maPhieuDatPhong VARCHAR(20)
maPhong VARCHAR(20)
thoiGianNhanPhong DATETIME
thoiGianTraPhong DATETIME      ← Cập nhật khi gia hạn
maLoaiDatPhong VARCHAR(20)
soNguoi INT
```

### Phong
```sql
maPhong VARCHAR(20)
soPhong NVARCHAR(100)
trangThai NVARCHAR(50)         ← "Đã đặt" / "Đang ở"
maLoaiPhong VARCHAR(20)
tang INT
```

---

## 📞 Quick Reference

| Cần | File | Lệnh |
|-----|------|------|
| Setup phòng nhận | NhanPhong_CreateRooms.sql | Chạy 1 lần |
| Setup phòng gia hạn | GiaHanPhong_CreateRooms.sql | Chạy 1 lần |
| Test nhận | NhanPhong_TestData.sql | Chạy mỗi lần test |
| Test gia hạn | GiaHanPhong_TestData.sql | Chạy mỗi lần test |
| Xóa test nhận | NhanPhong_CleanTestData.sql | Sau khi test |
| Xóa test gia hạn | GiaHanPhong_CleanTestData.sql | Sau khi test |
| Kiểm tra | *_CheckData.sql | Khi cần debug |

---

**Tất cả đã sẵn sàng để test! 🎉**

