
-- ===========================
-- VICTORYA HOTEL DATABASE v6
-- Cập nhật theo Model Classes
-- ===========================


-- Tạo database mới
CREATE DATABASE Victorya_Hotel_v7;
GO

USE Victorya_Hotel_v7;
GO

-- ===========================
-- TẠO CÁC BẢNG
-- ===========================

-- 1. TaiKhoan
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(100) NOT NULL,
    vaiTro VARCHAR(50) NOT NULL DEFAULT 'employee'
);

-- 2. NhanVien
CREATE TABLE NhanVien (
    maNhanVien VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(12),
    tenNhanVien NVARCHAR(100) NOT NULL,
    gioiTinh BIT DEFAULT 1,
    ngaySinh DATE,
    email VARCHAR(100),
    soDienThoai VARCHAR(20),
    ngayBatDau DATE DEFAULT GETDATE(),
    tenDangNhap VARCHAR(50),
    trangThai NVARCHAR(50) DEFAULT N'Đang làm việc',
    diaChi NVARCHAR(255),
    FOREIGN KEY (tenDangNhap) REFERENCES TaiKhoan(tenDangNhap)
);

-- 3. Ca
CREATE TABLE Ca (
    maCa VARCHAR(20) PRIMARY KEY,
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL
);

-- 4. CaLamViecNhanVien
CREATE TABLE CaLamViecNhanVien (
    maCaLamViec VARCHAR(20) PRIMARY KEY,
    maNhanVien VARCHAR(20) NOT NULL,
    ngay DATE NOT NULL,
    tienMoCa DECIMAL(18,2)DEFAULT 0,
    tienKetCa DECIMAL(18,2) DEFAULT 0,
    tongChi DECIMAL(10, 2) DEFAULT 0 NOT NULL,
    tongThu DECIMAL(10, 2) DEFAULT 0 NOT NULL,
    maCa VARCHAR(20) NOT NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang mở',
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maCa) REFERENCES Ca(maCa)
);

-- 5. LoaiPhong
CREATE TABLE LoaiPhong (
    maLoaiPhong VARCHAR(20) PRIMARY KEY,
    tenLoaiPhong NVARCHAR(100) NOT NULL,
    gia DECIMAL(18, 2) NOT NULL,
    soNguoiLonToiDa INT NOT NULL,
    soTreEmToiDa INT NOT NULL,
    ngayTao DATE DEFAULT GETDATE()
);

-- 6. Phong
CREATE TABLE Phong (
    maPhong VARCHAR(20) PRIMARY KEY,
    soPhong NVARCHAR(100) NOT NULL,
    trangThai NVARCHAR(50) DEFAULT N'Trống',
    maLoaiPhong VARCHAR(20) NOT NULL,
    tang INT NOT NULL,
    tinhTrang NVARCHAR(55) DEFAULT N'Tốt',
    [moTa] [nvarchar](500) NULL,
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong)
);

-- 7. LoaiDatPhong
CREATE TABLE LoaiDatPhong (
    maLoaiDatPhong VARCHAR(20) PRIMARY KEY,
    tenLoaiDatPhong NVARCHAR(100) NOT NULL,
    ngayTao DATE DEFAULT GETDATE()
);

-- 8. DichVu
CREATE TABLE DichVu (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(100) NOT NULL,
    gia DECIMAL(18, 2) NOT NULL,
    moTa NVARCHAR(255),
    donViTinh NVARCHAR(50)
);

-- 9. DichVu_LoaiPhong (Bảng liên kết)
CREATE TABLE DichVu_LoaiPhong (
    maDichVu VARCHAR(20),
    maLoaiPhong VARCHAR(20),
    PRIMARY KEY (maLoaiPhong, maDichVu),
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

-- 10. KhachHang
CREATE TABLE KhachHang (
    maKhachHang VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(20) UNIQUE NOT NULL,
    hoTen NVARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    ngayTao DATE DEFAULT GETDATE()
);

-- 11. PhieuDatPhong
CREATE TABLE PhieuDatPhong (
    maPhieuDatPhong VARCHAR(20) PRIMARY KEY,
    ngayTao DATE DEFAULT GETDATE(),
    maKhachHang VARCHAR(20) NOT NULL,
    trangThai NVARCHAR(20) DEFAULT N'Đã đặt',
    tienDatCoc DECIMAL(18, 2) DEFAULT 0,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang)
);

-- 12. ChiTietPhieuDatPhong
CREATE TABLE ChiTietPhieuDatPhong (
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    thoiGianNhanPhong DATETIME NOT NULL,
    thoiGianTraPhong DATETIME NOT NULL,
    maLoaiDatPhong VARCHAR(20) NOT NULL,
    soNguoi INT DEFAULT 1,
    trangThai NVARCHAR(50) DEFAULT N'Đã đặt',
    PRIMARY KEY (maPhieuDatPhong, maPhong),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong(maPhieuDatPhong),
    FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    FOREIGN KEY (maLoaiDatPhong) REFERENCES LoaiDatPhong(maLoaiDatPhong),
    CONSTRAINT CK_ChiTietPhieuDatPhong_Time CHECK (thoiGianTraPhong > thoiGianNhanPhong)
);

-- 13. ChiTietPhieuDatPhong_DichVu (Bảng liên kết)
CREATE TABLE ChiTietPhieuDatPhong_DichVu (
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    maDichVu VARCHAR(20),
    soLuong INT DEFAULT 1,
    PRIMARY KEY (maPhieuDatPhong, maPhong, maDichVu),
    FOREIGN KEY (maPhieuDatPhong, maPhong) REFERENCES ChiTietPhieuDatPhong(maPhieuDatPhong, maPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

-- 14. KhuyenMai
CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(20) PRIMARY KEY,
    tenKhuyenMai NVARCHAR(100) NOT NULL,
    ngayBatDau DATETIME NOT NULL,
    ngayKetThuc DATETIME NOT NULL,
    trangThai VARCHAR(50),
    heSo FLOAT NOT NULL,
    tongTienToiThieu DECIMAL(18, 2) DEFAULT 0,
    tongKhuyenMaiToiDa DECIMAL(18, 2) DEFAULT 0,
    CONSTRAINT CK_KhuyenMai_NgayApDung CHECK (ngayKetThuc >= ngayBatDau),
    CONSTRAINT CK_KhuyenMai_HeSo CHECK (heSo >= 0 AND heSo <= 1)
);

-- 15. HoaDon
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(20) PRIMARY KEY,
    ngayDat DATETIME DEFAULT GETDATE(),
    maKhachHang VARCHAR(20) NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    maKhuyenMai VARCHAR(20),
    ngayTao DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT N'Chưa thanh toán',
    tongTien DECIMAL(18, 2) DEFAULT 0,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai)
);

-- 16. ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(20),
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    ngayTao DATETIME DEFAULT GETDATE(),
    tongTien DECIMAL(18, 2) DEFAULT 0,
    PRIMARY KEY (maHoaDon, maPhieuDatPhong, maPhong),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maPhieuDatPhong, maPhong) REFERENCES ChiTietPhieuDatPhong(maPhieuDatPhong, maPhong)
);

-- 17. ChiTietHoaDon_DichVu (Bảng liên kết)
CREATE TABLE ChiTietHoaDon_DichVu (
    maHoaDon VARCHAR(20),
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    maDichVu VARCHAR(20),
    PRIMARY KEY (maHoaDon, maPhieuDatPhong, maPhong, maDichVu),
    FOREIGN KEY (maHoaDon, maPhieuDatPhong, maPhong) REFERENCES ChiTietHoaDon(maHoaDon, maPhieuDatPhong, maPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

-- 18. HuyPhong
CREATE TABLE HuyPhong (
    maHuyPhong INT IDENTITY PRIMARY KEY,
    maPhieuDatPhong VARCHAR(20) NOT NULL,
    lyDo NVARCHAR(255),
    ngayHuy DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong(maPhieuDatPhong)
);


-- ===========================
-- TẠO INDEX
-- ===========================

CREATE INDEX IX_NhanVien_TenDangNhap ON NhanVien(tenDangNhap);
CREATE INDEX IX_NhanVien_TrangThai ON NhanVien(trangThai);
CREATE INDEX IX_CaLamViecNhanVien_MaNhanVien ON CaLamViecNhanVien(maNhanVien);
CREATE INDEX IX_CaLamViecNhanVien_MaCa ON CaLamViecNhanVien(maCa);
CREATE INDEX IX_CaLamViecNhanVien_Ngay ON CaLamViecNhanVien(ngay);
CREATE INDEX IX_Phong_MaLoaiPhong ON Phong(maLoaiPhong);
CREATE INDEX IX_Phong_TrangThai ON Phong(trangThai);
CREATE INDEX IX_PhieuDatPhong_MaKhachHang ON PhieuDatPhong(maKhachHang);
CREATE INDEX IX_PhieuDatPhong_NgayTao ON PhieuDatPhong(ngayTao);
CREATE INDEX IX_PhieuDatPhong_TrangThai ON PhieuDatPhong(trangThai);
CREATE INDEX IX_KhachHang_CCCD ON KhachHang(CCCD);
CREATE INDEX IX_KhachHang_SoDienThoai ON KhachHang(soDienThoai);
CREATE INDEX IX_ChiTietPhieuDatPhong_MaPhieuDatPhong ON ChiTietPhieuDatPhong(maPhieuDatPhong);
CREATE INDEX IX_ChiTietPhieuDatPhong_MaPhong ON ChiTietPhieuDatPhong(maPhong);
CREATE INDEX IX_ChiTietPhieuDatPhong_ThoiGianNhanPhong ON ChiTietPhieuDatPhong(thoiGianNhanPhong);
CREATE INDEX IX_ChiTietPhieuDatPhong_ThoiGianTraPhong ON ChiTietPhieuDatPhong(thoiGianTraPhong);
CREATE INDEX IX_KhuyenMai_NgayBatDau ON KhuyenMai(ngayBatDau);
CREATE INDEX IX_KhuyenMai_NgayKetThuc ON KhuyenMai(ngayKetThuc);
CREATE INDEX IX_KhuyenMai_TrangThai ON KhuyenMai(trangThai);
CREATE INDEX IX_HoaDon_MaKhachHang ON HoaDon(maKhachHang);
CREATE INDEX IX_HoaDon_MaNhanVien ON HoaDon(maNhanVien);
CREATE INDEX IX_HoaDon_NgayDat ON HoaDon(ngayDat);
CREATE INDEX IX_HoaDon_NgayTao ON HoaDon(ngayTao);
CREATE INDEX IX_HoaDon_TrangThai ON HoaDon(trangThai);

-- ===========================
-- THÊM DỮ LIỆU MẪU
-- ===========================

-- 1. TaiKhoan
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro) VALUES
('0365271959', '$2a$10$9eypGo00I/fQB.j2X0ZyB.adLUV9/Kj/Mt8RUziE6UoFwTCYd.Ivi', 'admin'), -- mk: admin
('0365271958', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'); -- mk: hoang@123H

-- 2. NhanVien
INSERT INTO NhanVien (maNhanVien, CCCD, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap, trangThai, diaChi) VALUES
('NV001', '042204003399', N'Nguyễn Huy Hoàng', 1, '2004-08-27', 'nguyenhuyhoang270804@gmail.com', '0901234567', '2020-01-10', '0365271959', N'Đang làm việc', N'477/42 Nguyễn Văn Công, Gò Vấp'),
('NV002', '012345678910', N'Trần Thị Bình', 0, '1992-08-20', 'ttb@victorya.com', '0912345678', '2021-03-15', '0365271958', N'Đang làm việc', N'477/42 Nguyễn Văn Công, Gò Vấp');

-- Thêm TaiKhoan cho nhân viên và quản lý mới
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro) VALUES
('NV003', '$2a$10$9eypGo00I/fQB.j2X0ZyB.adLUV9/Kj/Mt8RUziE6UoFwTCYd.Ivi', 'admin'),
('NV004', '$2a$10$9eypGo00I/fQB.j2X0ZyB.adLUV9/Kj/Mt8RUziE6UoFwTCYd.Ivi', 'admin'),
('NV005', '$2a$10$9eypGo00I/fQB.j2X0ZyB.adLUV9/Kj/Mt8RUziE6UoFwTCYd.Ivi', 'admin'),
('NV006', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV007', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV008', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV009', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV010', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV011', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV012', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV013', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV014', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV015', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV016', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV017', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV018', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV019', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee'),
('NV020', '$2a$10$Wzi1CNeJidRvX5Q4SRdbneW0VZmk1T1i6mrv1Hh7vIi6OMQdGwu..', 'employee');

-- Thêm 3 quản lý (admin) và 15 nhân viên (employee)
INSERT INTO NhanVien (maNhanVien, CCCD, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap, trangThai, diaChi) VALUES
('NV003', '013345678901', N'Nguyễn Văn An', 1, '1988-05-10', 'nguyenvanan@example.com', '0902003003', '2022-01-01', 'NV003', N'Đang làm việc', N'1 Lê Lợi, Quận 1'),
('NV004', '014456789012', N'Trần Thị Bình An', 0, '1985-09-12', 'tranthibinan@example.com', '0902003004', '2022-02-01', 'NV004', N'Đang làm việc', N'2 Trần Hưng Đạo, Quận 1'),
('NV005', '015567890123', N'Lê Văn Cường', 1, '1990-11-20', 'levancuong@example.com', '0902003005', '2022-03-01', 'NV005', N'Đang làm việc', N'3 Nguyễn Huệ, Quận 1'),
('NV006', '016678901234', N'Phạm Thị Dung', 0, '1993-04-05', 'phamthidung@example.com', '0902003006', '2023-01-10', 'NV006', N'Đang làm việc', N'4 Phan Xích Long, Gò Vấp'),
('NV007', '017789012345', N'Vũ Minh Ánh', 1, '1994-06-17', 'vuminhE@example.com', '0902003007', '2023-02-12', 'NV007', N'Đang làm việc', N'5 Cách Mạng Tháng 8, Quận 3'),
('NV008', '018890123456', N'Bùi Thị Giang', 0, '1991-12-01', 'buithig@example.com', '0902003008', '2023-03-15', 'NV008', N'Đang làm việc', N'6 Lý Tự Trọng, Quận 1'),
('NV009', '019901234567', N'Đỗ Văn Huỳnh', 1, '1992-02-08', 'dovanh@example.com', '0902003009', '2023-04-20', 'NV009', N'Đang làm việc', N'7 Hai Bà Trưng, Quận 1'),
('NV010', '020012345678', N'Ngô Thị Ý', 0, '1995-07-30', 'ngothii@example.com', '0902003010', '2023-05-22', 'NV010', N'Đang làm việc', N'8 Điện Biên Phủ, Quận Bình Thạnh'),
('NV011', '021123456789', N'Phan Văn Thanh', 1, '1996-03-14', 'phanvanj@example.com', '0902003011', '2023-06-25', 'NV011', N'Đang làm việc', N'9 Phú Nhuận'),
('NV012', '022234567890', N'Nguyễn Thị Khánh Linh', 0, '1990-10-02', 'nguyenthik@example.com', '0902003012', '2023-07-30', 'NV012', N'Đang làm việc', N'10 Quận 2'),
('NV013', '023345678901', N'Lê Minh Lộc', 1, '1992-09-09', 'leminhl@example.com', '0902003013', '2023-08-12', 'NV013', N'Đang làm việc', N'11 Quận 4'),
('NV014', '024456789012', N'Huỳnh Thị Mai', 0, '1993-01-21', 'huynhthim@example.com', '0902003014', '2023-09-18', 'NV014', N'Đang làm việc', N'12 Quận 5'),
('NV015', '025567890123', N'Đặng Văn Nam', 1, '1994-05-11', 'dangvann@example.com', '0902003015', '2023-10-20', 'NV015', N'Đang làm việc', N'13 Quận 6'),
('NV016', '026678901234', N'Phùng Thị Hoa', 0, '1995-08-08', 'phungthio@example.com', '0902003016', '2023-11-22', 'NV016', N'Đang làm việc', N'14 Quận 7'),
('NV017', '027789012345', N'Nguyễn Văn Phong', 1, '1991-04-16', 'nguyenvanp@example.com', '0902003017', '2024-01-10', 'NV017', N'Đang làm việc', N'15 Quận 8'),
('NV018', '028890123456', N'Trần Thị Quỳnh Phương', 0, '1990-02-28', 'tranthiq@example.com', '0902003018', '2024-02-14', 'NV018', N'Đang làm việc', N'16 Quận 9'),
('NV019', '029901234567', N'Lâm Minh Châu', 1, '1993-11-11', 'lamminhr@example.com', '0902003019', '2024-03-18', 'NV019', N'Đang làm việc', N'17 Thủ Đức'),
('NV020', '030012345678', N'Hoàng Thị Sen', 0, '1992-07-07', 'hoangthis@example.com', '0902003020', '2024-04-20', 'NV020', N'Đang làm việc', N'18 Quận 10');

-- 3. Ca làm việc
INSERT INTO Ca (maCa, ngayBatDau, ngayKetThuc) VALUES
('CA-20251020-1', '2025-10-20', '2025-10-20'),
('CA-20251020-2', '2025-10-20', '2025-10-20'),
('CA-20251020-3', '2025-10-20', '2025-10-20'),
('CA-20251021-1', '2025-10-21', '2025-10-21'),
('CA-20251021-2', '2025-10-21', '2025-10-21'),
('CA-20251021-3', '2025-10-21', '2025-10-21'),
('CA-20251022-1', '2025-10-22', '2025-10-22'),
('CA-20251022-2', '2025-10-22', '2025-10-22'),
('CA-20251022-3', '2025-10-22', '2025-10-22'),
('CA-20251023-1', '2025-10-23', '2025-10-23'),
('CA-20251023-2', '2025-10-23', '2025-10-23'),
('CA-20251023-3', '2025-10-23', '2025-10-23');

-- 3.1. Ca làm việc nhân viên
INSERT INTO CaLamViecNhanVien (maCaLamViec, maNhanVien, ngay, tienMoCa, tienKetCa, maCa, trangThai) VALUES
('CLV001', 'NV002', '2025-10-20', 500000, 2800000, 'CA-20251020-1', N'Đã hoàn thành'),
('CLV004', 'NV002', '2025-10-21', 500000, 3100000, 'CA-20251021-1', N'Đã hoàn thành'),
('CLV008', 'NV002', '2025-10-22', 2900000, 4700000, 'CA-20251022-2', N'Đã hoàn thành'),
('CLV010', 'NV002', '2025-10-23', 500000, NULL, 'CA-20251023-1', N'Đang mở');

-- 4. LoaiPhong
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, gia, soNguoiLonToiDa, soTreEmToiDa, ngayTao) VALUES
('LP01', N'Phòng đơn', 60000, 2, 1, '2024-01-01'),
('LP02', N'Phòng đôi', 80000, 3, 2, '2024-01-01'),
('LP03', N'Phòng gia đình', 100000, 4, 2, '2024-01-01');

-- 5. DichVu
INSERT INTO DichVu (maDichVu, tenDichVu, gia, moTa, donViTinh) VALUES
('DV-12345', N'Nước suối', 10000, N'Nước suối Lavie 500ml', N'Chai'),
('DV-23456', N'Coca Cola', 15000, N'Nước ngọt Coca Cola 330ml', N'Lon'),
('DV-34567', N'Bia Heineken', 25000, N'Bia Heineken 330ml', N'Lon'),
('DV-45678', N'Snack', 20000, N'Snack khoai tây Poca', N'Gói'),
('DV-56789', N'Trái cây', 50000, N'Đĩa trái cây tươi', N'Đĩa'),
('DV-67890', N'Giặt ủi', 30000, N'Dịch vụ giặt ủi quần áo', N'Kg'),
('DV-78901', N'Đưa đón sân bay', 300000, N'Dịch vụ đưa đón sân bay', N'Lượt'),
('DV-89012', N'Ăn sáng buffet', 100000, N'Buffet sáng đa dạng món', N'Suất'),
('DV-01234', N'Karaoke', 150000, N'Phòng karaoke theo giờ', N'Giờ'),
('DV-90123', N'Dịch vụ đặc biệt', 500000, N'Dịch vụ đặc biệt cho phòng VIP', N'Lượt');

-- 6. DichVu_LoaiPhong (CHỈ PHÒNG VIP CÓ DỊCH VỤ)
INSERT INTO DichVu_LoaiPhong (maDichVu, maLoaiPhong) VALUES
('DV-12345', 'LP02'),
('DV-23456', 'LP02'),
('DV-34567', 'LP02'),
('DV-45678', 'LP02'),
('DV-56789', 'LP02'),
('DV-67890', 'LP02'),
('DV-78901', 'LP02'),
('DV-89012', 'LP02'),
('DV-90123', 'LP02'),
('DV-01234', 'LP02');

-- 7. Phong (40 phòng, 8 tầng - Tầng 1-6: Thường, Tầng 7-8: VIP)
-- TẦNG 1 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0001', '101', N'Đang ở', 'LP01', 1, N'Tốt'),
('P-0002', '102', N'Trống', 'LP01', 1, N'Tốt'),
('P-0003', '103', N'Đang ở', 'LP03', 1, N'Tốt'),
('P-0004', '104', N'Trống', 'LP03', 1, N'Tốt'),
('P-0005', '105', N'Đã đặt', 'LP03', 1, N'Tốt');

-- TẦNG 2 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0006', '201', N'Đang ở', 'LP01', 2, N'Tốt'),
('P-0007', '202', N'Trống', 'LP01', 2, N'Tốt'),
('P-0008', '203', N'Trống', 'LP02', 2, N'Tốt'),
('P-0009', '204', N'Đang ở', 'LP02', 2, N'Tốt'),
('P-0010', '205', N'Trống', 'LP03', 2, N'Tốt');

-- TẦNG 3 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0011', '301', N'Trống', 'LP01', 3, N'Tốt'),
('P-0012', '302', N'Đã đặt', 'LP01', 3, N'Tốt'),
('P-0013', '303', N'Trống', 'LP02', 3, N'Tốt'),
('P-0014', '304', N'Bảo trì', 'LP02', 3, N'Cần sửa chữa'),
('P-0015', '305', N'Trống', 'LP03', 3, N'Tốt');

-- TẦNG 4 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0016', '401', N'Trống', 'LP01', 4, N'Tốt'),
('P-0017', '402', N'Đang ở', 'LP01', 4, N'Tốt'),
('P-0018', '403', N'Trống', 'LP02', 4, N'Tốt'),
('P-0019', '404', N'Trống', 'LP02', 4, N'Tốt'),
('P-0020', '405', N'Trống', 'LP03', 4, N'Tốt');

-- TẦNG 5 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0021', '501', N'Trống', 'LP01', 5, N'Tốt'),
('P-0022', '502', N'Trống', 'LP01', 5, N'Tốt'),
('P-0023', '503', N'Đang ở', 'LP02', 5, N'Tốt'),
('P-0024', '504', N'Trống', 'LP02', 5, N'Tốt'),
('P-0025', '505', N'Trống', 'LP03', 5, N'Tốt');

-- TẦNG 6 - Phòng Thường
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0026', '601', N'Trống', 'LP01', 6, N'Tốt'),
('P-0027', '602', N'Trống', 'LP01', 6, N'Tốt'),
('P-0028', '603', N'Trống', 'LP02', 6, N'Tốt'),
('P-0029', '604', N'Trống', 'LP02', 6, N'Tốt'),
('P-0030', '605', N'Trống', 'LP03', 6, N'Tốt');

-- TẦNG 7 - Phòng VIP
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0031', '701', N'Đang ở', 'LP01', 7, N'Tốt'),
('P-0032', '702', N'Trống', 'LP01', 7, N'Tốt'),
('P-0033', '703', N'Đang ở', 'LP02', 7, N'Tốt'),
('P-0034', '704', N'Trống', 'LP02', 7, N'Tốt'),
('P-0035', '705', N'Đã đặt', 'LP03', 7, N'Tốt');

-- TẦNG 8 - Phòng VIP
INSERT INTO Phong (maPhong, soPhong, trangThai, maLoaiPhong, tang, tinhTrang) VALUES
('P-0036', '801', N'Đang ở', 'LP01', 8, N'Tốt'),
('P-0037', '802', N'Trống', 'LP01', 8, N'Tốt'),
('P-0038', '803', N'Đã đặt', 'LP02', 8, N'Tốt'),
('P-0039', '804', N'Trống', 'LP02', 8, N'Tốt'),
('P-0040', '805', N'Trống', 'LP03', 8, N'Tốt');

-- 8. LoaiDatPhong
INSERT INTO LoaiDatPhong (maLoaiDatPhong, tenLoaiDatPhong, ngayTao) VALUES
('LDP01', N'Online', '2024-01-01'),
('LDP02', N'Offline', '2024-01-01');

-- 9. KhachHang (30 khách hàng)
INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES
('KH001', '001234567890', N'Nguyễn Văn Hùng', '0987654321', 'nvhung@gmail.com', '2025-10-20'),
('KH002', '002345678901', N'Trần Thị Lan', '0976543210', 'ttlan@gmail.com', '2025-10-21'),
('KH003', '003456789012', N'Lê Minh Tuấn', '0965432109', 'lmtuan@gmail.com', '2025-10-21'),
('KH004', '004567890123', N'Phạm Thu Hà', '0954321098', 'ptha@gmail.com', '2025-10-22'),
('KH005', '005678901234', N'Hoàng Đức Anh', '0943210987', 'hdanh@gmail.com', '2025-10-22'),
('KH006', '006789012345', N'Vũ Thị Mai', '0932109876', 'vtmai@gmail.com', '2025-10-20'),
('KH007', '007890123456', N'Đỗ Văn Nam', '0921098765', 'dvnam@gmail.com', '2025-10-21'),
('KH008', '008901234567', N'Bùi Thị Hoa', '0910987654', 'bthoa@gmail.com', '2025-10-23'),
('KH009', '009012345678', N'Trương Văn Phong', '0909876543', 'tvphong@gmail.com', '2025-10-23'),
('KH010', '010123456789', N'Ngô Thị Linh', '0898765432', 'ntlinh@gmail.com', '2025-10-23'),
('KH011', '011234567890', N'Phan Văn Đức', '0887654321', 'pvduc@gmail.com', '2025-10-18'),
('KH012', '012345678901', N'Đinh Thị Ngọc', '0876543210', 'dtngoc@gmail.com', '2025-10-19'),
('KH013', '013456789012', N'Mai Văn Tâm', '0865432109', 'mvtam@gmail.com', '2025-10-19'),
('KH014', '014567890123', N'Lý Thị Thu', '0854321098', 'ltthu@gmail.com', '2025-10-20'),
('KH015', '015678901234', N'Tô Văn Long', '0843210987', 'tvlong@gmail.com', '2025-10-20'),
('KH016', '016789012345', N'Nguyễn Thị Lan', '0832109876', 'ntlan@gmail.com', '2025-10-15'),
('KH017', '017890123456', N'Hoàng Văn Minh', '0821098765', 'hvminh@gmail.com', '2025-10-16'),
('KH018', '018901234567', N'Trịnh Thị Hương', '0810987654', 'tthuong@gmail.com', '2025-10-17'),
('KH019', '019012345678', N'Lê Văn Cường', '0909876543', 'lvcuong@gmail.com', '2025-10-18'),
('KH020', '020123456789', N'Phạm Thị Nhung', '0898765432', 'ptnhung@gmail.com', '2025-10-19'),
('KH021', '021234567890', N'Đặng Văn Tuấn', '0887654321', 'dvtuan@gmail.com', '2025-10-20'),
('KH022', '022345678901', N'Võ Thị Loan', '0876543210', 'vtloan@gmail.com', '2025-10-21'),
('KH023', '023456789012', N'Bùi Văn Hải', '0865432109', 'bvhai@gmail.com', '2025-10-22'),
('KH024', '024567890123', N'Dương Thị Kim', '0854321098', 'dtkim@gmail.com', '2025-10-15'),
('KH025', '025678901234', N'Cao Văn Sơn', '0843210987', 'cvson@gmail.com', '2025-10-16'),
('KH026', '026789012345', N'Lưu Thị Trang', '0832109876', 'lttrang@gmail.com', '2025-10-17'),
('KH027', '027890123456', N'Hồ Văn Thắng', '0821098765', 'hvthang@gmail.com', '2025-10-18'),
('KH028', '028901234567', N'Phan Thị Oanh', '0810987654', 'ptoanh@gmail.com', '2025-10-19'),
('KH029', '029012345678', N'Trần Văn Tùng', '0909876543', 'tvtung@gmail.com', '2025-10-20'),
('KH030', '030123456789', N'Ngô Thị Yến', '0898765432', 'ntyen@gmail.com', '2025-10-21');

-- 10. KhuyenMai
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) VALUES
('KM-0001', N'Giảm 10% cho hóa đơn trên 1 triệu', '2025-10-01 00:00:00', '2025-12-31 23:59:59', 'Đang áp dụng', 0.10, 1000000, 200000),
('KM-0002', N'Giảm 15% cho hóa đơn trên 2 triệu', '2025-10-01 00:00:00', '2025-12-31 23:59:59', 'Đang áp dụng', 0.15, 2000000, 400000),
('KM-0003', N'Giảm 20% cho hóa đơn trên 5 triệu', '2025-10-01 00:00:00', '2025-12-31 23:59:59', 'Đang áp dụng', 0.20, 5000000, 1000000),
('KM-0004', N'Khách hàng VIP - Giảm 25%', '2025-10-01 00:00:00', '2025-12-31 23:59:59', 'Đang áp dụng', 0.25, 3000000, 1500000),
('KM-0005', N'Khuyến mãi hè - Giảm 5%', '2025-09-01 00:00:00', '2025-09-30 23:59:59', 'Đang áp dụng', 0.05, 500000, 100000);

INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang, trangThai, tienDatCoc) VALUES
('PDP-20251220-0001','2025-12-20','KH001',N'Đã hoàn thành',100000),
('PDP-20251221-0002','2025-12-21','KH001',N'Đã hoàn thành',100000),
('PDP-20251222-0003','2025-12-22','KH001',N'Đã hoàn thành',100000),
('PDP-20251223-0004','2025-12-23','KH001',N'Đã hoàn thành',100000),
('PDP-20251224-0005','2025-12-24','KH001',N'Đã hoàn thành',100000),
('PDP-20251225-0006','2025-12-25','KH001',N'Đã hoàn thành',100000),
('PDP-20251226-0007','2025-12-26','KH001',N'Đã hoàn thành',100000),
('PDP-20251227-0008','2025-12-27','KH001',N'Đã hoàn thành',100000),
('PDP-20251228-0009','2025-12-28','KH001',N'Đã hoàn thành',100000),
('PDP-20251229-0010','2025-12-29','KH001',N'Đã hoàn thành',100000),
('PDP-20251230-0011','2025-12-30','KH001',N'Đã hoàn thành',100000),
('PDP-20251231-0012','2025-12-31','KH001',N'Đã hoàn thành',100000),
('PDP-20250101-0013','2025-01-01','KH001',N'Đã hoàn thành',100000),
('PDP-20250102-0014','2025-01-02','KH001',N'Đã hoàn thành',100000),
('PDP-20250103-0015','2025-01-03','KH001',N'Đã hoàn thành',100000),
('PDP-20250104-0016','2025-01-04','KH001',N'Đã hoàn thành',100000),
('PDP-20250105-0017','2025-01-05','KH001',N'Đã hoàn thành',100000),
('PDP-20250106-0018','2025-01-06','KH001',N'Đã hoàn thành',100000),
('PDP-20250107-0019','2025-01-07','KH001',N'Đã hoàn thành',100000),
('PDP-20250108-0020','2025-01-08','KH001',N'Đã hoàn thành',100000),
('PDP-20250109-0021','2025-01-09','KH001',N'Đã hoàn thành',100000),
('PDP-20250110-0022','2025-01-10','KH001',N'Đã hoàn thành',100000),
('PDP-20250111-0023','2025-01-11','KH001',N'Đã hoàn thành',100000),
('PDP-20250112-0024','2025-01-12','KH001',N'Đã hoàn thành',100000),
('PDP-20250113-0025','2025-01-13','KH001',N'Đã hoàn thành',100000),
('PDP-20250114-0026','2025-01-14','KH001',N'Đã hoàn thành',100000),
('PDP-20250115-0027','2025-01-15','KH001',N'Đã hoàn thành',100000),
('PDP-20250116-0028','2025-01-16','KH001',N'Đã hoàn thành',100000),
('PDP-20250117-0029','2025-01-17','KH001',N'Đã hoàn thành',100000),
('PDP-20250118-0030','2025-01-18','KH001',N'Đã hoàn thành',100000),
('PDP-20250119-0031','2025-01-19','KH001',N'Đã hoàn thành',100000),
('PDP-20250120-0032','2025-01-20','KH001',N'Đã hoàn thành',100000),
('PDP-20250121-0033','2025-01-21','KH001',N'Đã hoàn thành',100000),
('PDP-20250122-0034','2025-01-22','KH001',N'Đã hoàn thành',100000),
('PDP-20250123-0035','2025-01-23','KH001',N'Đã hoàn thành',100000),
('PDP-20250124-0036','2025-01-24','KH001',N'Đã hoàn thành',100000),
('PDP-20250125-0037','2025-01-25','KH001',N'Đã hoàn thành',100000),
('PDP-20250126-0038','2025-01-26','KH001',N'Đã hoàn thành',100000),
('PDP-20250127-0039','2025-01-27','KH001',N'Đã hoàn thành',100000),
('PDP-20250128-0040','2025-01-28','KH001',N'Đã hoàn thành',100000),
('PDP-20250129-0041','2025-01-29','KH001',N'Đã hoàn thành',100000),
('PDP-20250130-0042','2025-01-30','KH001',N'Đã hoàn thành',100000),
('PDP-20250131-0043','2025-01-31','KH001',N'Đã hoàn thành',100000),
('PDP-20250201-0044','2025-02-01','KH001',N'Đã hoàn thành',100000),
('PDP-20250202-0045','2025-02-02','KH001',N'Đã hoàn thành',100000),
('PDP-20250203-0046','2025-02-03','KH001',N'Đã hoàn thành',100000),
('PDP-20250204-0047','2025-02-04','KH001',N'Đã hoàn thành',100000),
('PDP-20250205-0048','2025-02-05','KH001',N'Đã hoàn thành',100000),
('PDP-20250206-0049','2025-02-06','KH001',N'Đã hoàn thành',100000),
('PDP-20250207-0050','2025-02-07','KH001',N'Đã hoàn thành',100000);


INSERT INTO ChiTietPhieuDatPhong
(maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) VALUES
('PDP-20251220-0001','P-0001','2025-12-20 1:00:00','2025-12-21 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251221-0002','P-0001','2025-12-21 1:00:00','2025-12-22 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251222-0003','P-0001','2025-12-22 1:00:00','2025-12-23 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251223-0004','P-0001','2025-12-23 1:00:00','2025-12-24 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251224-0005','P-0001','2025-12-24 1:00:00','2025-12-25 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251225-0006','P-0001','2025-12-25 1:00:00','2025-12-26 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251226-0007','P-0001','2025-12-26 1:00:00','2025-12-27 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251227-0008','P-0001','2025-12-27 1:00:00','2025-12-28 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251228-0009','P-0001','2025-12-28 1:00:00','2025-12-29 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251229-0010','P-0001','2025-12-29 1:00:00','2025-12-30 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251230-0011','P-0001','2025-12-30 1:00:00','2025-12-31 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20251231-0012','P-0001','2025-12-31 1:00:00','2025-12-31 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250101-0013','P-0001','2025-01-01 1:00:00','2025-01-02 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250102-0014','P-0001','2025-01-02 1:00:00','2025-01-03 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250103-0015','P-0001','2025-01-03 1:00:00','2025-01-04 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250104-0016','P-0001','2025-01-04 1:00:00','2025-01-05 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250105-0017','P-0001','2025-01-05 1:00:00','2025-01-06 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250106-0018','P-0001','2025-01-06 1:00:00','2025-01-07 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250107-0019','P-0001','2025-01-07 1:00:00','2025-01-08 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250108-0020','P-0001','2025-01-08 1:00:00','2025-01-09 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250109-0021','P-0001','2025-01-09 1:00:00','2025-01-10 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250110-0022','P-0001','2025-01-10 1:00:00','2025-01-11 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250111-0023','P-0001','2025-01-11 1:00:00','2025-01-12 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250112-0024','P-0001','2025-01-12 1:00:00','2025-01-13 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250113-0025','P-0001','2025-01-13 1:00:00','2025-01-14 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250114-0026','P-0001','2025-01-14 1:00:00','2025-01-15 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250115-0027','P-0001','2025-01-15 1:00:00','2025-01-16 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250116-0028','P-0001','2025-01-16 1:00:00','2025-01-17 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250117-0029','P-0001','2025-01-17 1:00:00','2025-01-18 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250118-0030','P-0001','2025-01-18 1:00:00','2025-01-19 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250119-0031','P-0001','2025-01-19 1:00:00','2025-01-20 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250120-0032','P-0001','2025-01-20 1:00:00','2025-01-21 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250121-0033','P-0001','2025-01-21 1:00:00','2025-01-22 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250122-0034','P-0001','2025-01-22 1:00:00','2025-01-23 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250123-0035','P-0001','2025-01-23 1:00:00','2025-01-24 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250124-0036','P-0001','2025-01-24 1:00:00','2025-01-25 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250125-0037','P-0001','2025-01-25 1:00:00','2025-01-26 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250126-0038','P-0001','2025-01-26 1:00:00','2025-01-27 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250127-0039','P-0001','2025-01-27 1:00:00','2025-01-28 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250128-0040','P-0001','2025-01-28 1:00:00','2025-01-29 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250129-0041','P-0001','2025-01-29 1:00:00','2025-01-30 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250130-0042','P-0001','2025-01-30 1:00:00','2025-01-31 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250131-0043','P-0001','2025-01-31 1:00:00','2025-02-01 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250201-0044','P-0001','2025-02-01 1:00:00','2025-02-02 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250202-0045','P-0001','2025-02-02 1:00:00','2025-02-03 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250203-0046','P-0001','2025-02-03 1:00:00','2025-02-04 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250204-0047','P-0001','2025-02-04 1:00:00','2025-02-05 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250205-0048','P-0001','2025-02-05 1:00:00','2025-02-06 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250206-0049','P-0001','2025-02-06 1:00:00','2025-02-07 23:00:00','LDP01',2,N'Đã hoàn thành'),
('PDP-20250207-0050','P-0001','2025-02-07 1:00:00','2025-02-08 23:00:00','LDP01',2,N'Đã hoàn thành');

INSERT INTO HoaDon
(maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien) VALUES
('HD-20251220-00000001','2025-12-20 15:00:00','KH001','NV001',NULL,'2025-12-20 15:00:00',N'Đã thanh toán',150000),
('HD-20251221-00000002','2025-12-21 15:00:00','KH001','NV001',NULL,'2025-12-21 15:00:00',N'Đã thanh toán',150000),
('HD-20251222-00000003','2025-12-22 15:00:00','KH001','NV001',NULL,'2025-12-22 15:00:00',N'Đã thanh toán',150000),
('HD-20251223-00000004','2025-12-23 15:00:00','KH001','NV001',NULL,'2025-12-23 15:00:00',N'Đã thanh toán',150000),
('HD-20251224-00000005','2025-12-24 15:00:00','KH001','NV001',NULL,'2025-12-24 15:00:00',N'Đã thanh toán',150000),
('HD-20251225-00000006','2025-12-25 15:00:00','KH001','NV001',NULL,'2025-12-25 15:00:00',N'Đã thanh toán',150000),
('HD-20251226-00000007','2025-12-26 15:00:00','KH001','NV001',NULL,'2025-12-26 15:00:00',N'Đã thanh toán',150000),
('HD-20251227-00000008','2025-12-27 15:00:00','KH001','NV001',NULL,'2025-12-27 15:00:00',N'Đã thanh toán',150000),
('HD-20251228-00000009','2025-12-28 15:00:00','KH001','NV001',NULL,'2025-12-28 15:00:00',N'Đã thanh toán',150000),
('HD-20251229-00000010','2025-12-29 15:00:00','KH001','NV001',NULL,'2025-12-29 15:00:00',N'Đã thanh toán',150000),
('HD-20251230-00000011','2025-12-30 15:00:00','KH001','NV001',NULL,'2025-12-30 15:00:00',N'Đã thanh toán',150000),
('HD-20251231-00000012','2025-12-31 15:00:00','KH001','NV001',NULL,'2025-12-31 15:00:00',N'Đã thanh toán',150000),
('HD-20250101-00000013','2025-01-01 15:00:00','KH001','NV001',NULL,'2025-01-01 15:00:00',N'Đã thanh toán',150000),
('HD-20250102-00000014','2025-01-02 15:00:00','KH001','NV001',NULL,'2025-01-02 15:00:00',N'Đã thanh toán',150000),
('HD-20250103-00000015','2025-01-03 15:00:00','KH001','NV001',NULL,'2025-01-03 15:00:00',N'Đã thanh toán',150000),
('HD-20250104-00000016','2025-01-04 15:00:00','KH001','NV001',NULL,'2025-01-04 15:00:00',N'Đã thanh toán',150000),
('HD-20250105-00000017','2025-01-05 15:00:00','KH001','NV001',NULL,'2025-01-05 15:00:00',N'Đã thanh toán',150000),
('HD-20250106-00000018','2025-01-06 15:00:00','KH001','NV001',NULL,'2025-01-06 15:00:00',N'Đã thanh toán',150000),
('HD-20250107-00000019','2025-01-07 15:00:00','KH001','NV001',NULL,'2025-01-07 15:00:00',N'Đã thanh toán',150000),
('HD-20250108-00000020','2025-01-08 15:00:00','KH001','NV001',NULL,'2025-01-08 15:00:00',N'Đã thanh toán',150000),
('HD-20250109-00000021','2025-01-09 15:00:00','KH001','NV001',NULL,'2025-01-09 15:00:00',N'Đã thanh toán',150000),
('HD-20250110-00000022','2025-01-10 15:00:00','KH001','NV001',NULL,'2025-01-10 15:00:00',N'Đã thanh toán',150000),
('HD-20250111-00000023','2025-01-11 15:00:00','KH001','NV001',NULL,'2025-01-11 15:00:00',N'Đã thanh toán',150000),
('HD-20250112-00000024','2025-01-12 15:00:00','KH001','NV001',NULL,'2025-01-12 15:00:00',N'Đã thanh toán',150000),
('HD-20250113-00000025','2025-01-13 15:00:00','KH001','NV001',NULL,'2025-01-13 15:00:00',N'Đã thanh toán',150000),
('HD-20250114-00000026','2025-01-14 15:00:00','KH001','NV001',NULL,'2025-01-14 15:00:00',N'Đã thanh toán',150000),
('HD-20250115-00000027','2025-01-15 15:00:00','KH001','NV001',NULL,'2025-01-15 15:00:00',N'Đã thanh toán',150000),
('HD-20250116-00000028','2025-01-16 15:00:00','KH001','NV001',NULL,'2025-01-16 15:00:00',N'Đã thanh toán',150000),
('HD-20250117-00000029','2025-01-17 15:00:00','KH001','NV001',NULL,'2025-01-17 15:00:00',N'Đã thanh toán',150000),
('HD-20250118-00000030','2025-01-18 15:00:00','KH001','NV001',NULL,'2025-01-18 15:00:00',N'Đã thanh toán',150000),
('HD-20250119-00000031','2025-01-19 15:00:00','KH001','NV001',NULL,'2025-01-19 15:00:00',N'Đã thanh toán',150000),
('HD-20250120-00000032','2025-01-20 15:00:00','KH001','NV001',NULL,'2025-01-20 15:00:00',N'Đã thanh toán',150000),
('HD-20250121-00000033','2025-01-21 15:00:00','KH001','NV001',NULL,'2025-01-21 15:00:00',N'Đã thanh toán',150000),
('HD-20250122-00000034','2025-01-22 15:00:00','KH001','NV001',NULL,'2025-01-22 15:00:00',N'Đã thanh toán',150000),
('HD-20250123-00000035','2025-01-23 15:00:00','KH001','NV001',NULL,'2025-01-23 15:00:00',N'Đã thanh toán',150000),
('HD-20250124-00000036','2025-01-24 15:00:00','KH001','NV001',NULL,'2025-01-24 15:00:00',N'Đã thanh toán',150000),
('HD-20250125-00000037','2025-01-25 15:00:00','KH001','NV001',NULL,'2025-01-25 15:00:00',N'Đã thanh toán',150000),
('HD-20250126-00000038','2025-01-26 15:00:00','KH001','NV001',NULL,'2025-01-26 15:00:00',N'Đã thanh toán',150000),
('HD-20250127-00000039','2025-01-27 15:00:00','KH001','NV001',NULL,'2025-01-27 15:00:00',N'Đã thanh toán',150000),
('HD-20250128-00000040','2025-01-28 15:00:00','KH001','NV001',NULL,'2025-01-28 15:00:00',N'Đã thanh toán',150000),
('HD-20250129-00000041','2025-01-29 15:00:00','KH001','NV001',NULL,'2025-01-29 15:00:00',N'Đã thanh toán',150000),
('HD-20250130-00000042','2025-01-30 15:00:00','KH001','NV001',NULL,'2025-01-30 15:00:00',N'Đã thanh toán',150000),
('HD-20250131-00000043','2025-01-31 15:00:00','KH001','NV001',NULL,'2025-01-31 15:00:00',N'Đã thanh toán',150000),
('HD-20250201-00000044','2025-02-01 15:00:00','KH001','NV001',NULL,'2025-02-01 15:00:00',N'Đã thanh toán',150000),
('HD-20250202-00000045','2025-02-02 15:00:00','KH001','NV001',NULL,'2025-02-02 15:00:00',N'Đã thanh toán',150000),
('HD-20250203-00000046','2025-02-03 15:00:00','KH001','NV001',NULL,'2025-02-03 15:00:00',N'Đã thanh toán',150000),
('HD-20250204-00000047','2025-02-04 15:00:00','KH001','NV001',NULL,'2025-02-04 15:00:00',N'Đã thanh toán',150000),
('HD-20250205-00000048','2025-02-05 15:00:00','KH001','NV001',NULL,'2025-02-05 15:00:00',N'Đã thanh toán',150000),
('HD-20250206-00000049','2025-02-06 15:00:00','KH001','NV001',NULL,'2025-02-06 15:00:00',N'Đã thanh toán',150000),
('HD-20250207-00000050','2025-02-07 15:00:00','KH001','NV001',NULL,'2025-02-07 15:00:00',N'Đã thanh toán',150000);


INSERT INTO ChiTietHoaDon
(maHoaDon, maPhieuDatPhong, maPhong, ngayTao, tongTien) VALUES
('HD-20251220-00000001','PDP-20251220-0001','P-0001','2025-12-20 15:00:00',150000),
('HD-20251221-00000002','PDP-20251221-0002','P-0001','2025-12-21 15:00:00',150000),
('HD-20251222-00000003','PDP-20251222-0003','P-0001','2025-12-22 15:00:00',150000),
('HD-20251223-00000004','PDP-20251223-0004','P-0001','2025-12-23 15:00:00',150000),
('HD-20251224-00000005','PDP-20251224-0005','P-0001','2025-12-24 15:00:00',150000),
('HD-20251225-00000006','PDP-20251225-0006','P-0001','2025-12-25 15:00:00',150000),
('HD-20251226-00000007','PDP-20251226-0007','P-0001','2025-12-26 15:00:00',150000),
('HD-20251227-00000008','PDP-20251227-0008','P-0001','2025-12-27 15:00:00',150000),
('HD-20251228-00000009','PDP-20251228-0009','P-0001','2025-12-28 15:00:00',150000),
('HD-20251229-00000010','PDP-20251229-0010','P-0001','2025-12-29 15:00:00',150000),
('HD-20251230-00000011','PDP-20251230-0011','P-0001','2025-12-30 15:00:00',150000),
('HD-20251231-00000012','PDP-20251231-0012','P-0001','2025-12-31 15:00:00',150000),
('HD-20250101-00000013','PDP-20250101-0013','P-0001','2025-01-01 15:00:00',150000),
('HD-20250102-00000014','PDP-20250102-0014','P-0001','2025-01-02 15:00:00',150000),
('HD-20250103-00000015','PDP-20250103-0015','P-0001','2025-01-03 15:00:00',150000),
('HD-20250104-00000016','PDP-20250104-0016','P-0001','2025-01-04 15:00:00',150000),
('HD-20250105-00000017','PDP-20250105-0017','P-0001','2025-01-05 15:00:00',150000),
('HD-20250106-00000018','PDP-20250106-0018','P-0001','2025-01-06 15:00:00',150000),
('HD-20250107-00000019','PDP-20250107-0019','P-0001','2025-01-07 15:00:00',150000),
('HD-20250108-00000020','PDP-20250108-0020','P-0001','2025-01-08 15:00:00',150000),
('HD-20250109-00000021','PDP-20250109-0021','P-0001','2025-01-09 15:00:00',150000),
('HD-20250110-00000022','PDP-20250110-0022','P-0001','2025-01-10 15:00:00',150000),
('HD-20250111-00000023','PDP-20250111-0023','P-0001','2025-01-11 15:00:00',150000),
('HD-20250112-00000024','PDP-20250112-0024','P-0001','2025-01-12 15:00:00',150000),
('HD-20250113-00000025','PDP-20250113-0025','P-0001','2025-01-13 15:00:00',150000),
('HD-20250114-00000026','PDP-20250114-0026','P-0001','2025-01-14 15:00:00',150000),
('HD-20250115-00000027','PDP-20250115-0027','P-0001','2025-01-15 15:00:00',150000),
('HD-20250116-00000028','PDP-20250116-0028','P-0001','2025-01-16 15:00:00',150000),
('HD-20250117-00000029','PDP-20250117-0029','P-0001','2025-01-17 15:00:00',150000),
('HD-20250118-00000030','PDP-20250118-0030','P-0001','2025-01-18 15:00:00',150000),
('HD-20250119-00000031','PDP-20250119-0031','P-0001','2025-01-19 15:00:00',150000),
('HD-20250120-00000032','PDP-20250120-0032','P-0001','2025-01-20 15:00:00',150000),
('HD-20250121-00000033','PDP-20250121-0033','P-0001','2025-01-21 15:00:00',150000),
('HD-20250122-00000034','PDP-20250122-0034','P-0001','2025-01-22 15:00:00',150000),
('HD-20250123-00000035','PDP-20250123-0035','P-0001','2025-01-23 15:00:00',150000),
('HD-20250124-00000036','PDP-20250124-0036','P-0001','2025-01-24 15:00:00',150000),
('HD-20250125-00000037','PDP-20250125-0037','P-0001','2025-01-25 15:00:00',150000),
('HD-20250126-00000038','PDP-20250126-0038','P-0001','2025-01-26 15:00:00',150000),
('HD-20250127-00000039','PDP-20250127-0039','P-0001','2025-01-27 15:00:00',150000),
('HD-20250128-00000040','PDP-20250128-0040','P-0001','2025-01-28 15:00:00',150000),
('HD-20250129-00000041','PDP-20250129-0041','P-0001','2025-01-29 15:00:00',150000),
('HD-20250130-00000042','PDP-20250130-0042','P-0001','2025-01-30 15:00:00',150000),
('HD-20250131-00000043','PDP-20250131-0043','P-0001','2025-01-31 15:00:00',150000),
('HD-20250201-00000044','PDP-20250201-0044','P-0001','2025-02-01 15:00:00',150000),
('HD-20250202-00000045','PDP-20250202-0045','P-0001','2025-02-02 15:00:00',150000),
('HD-20250203-00000046','PDP-20250203-0046','P-0001','2025-02-03 15:00:00',150000),
('HD-20250204-00000047','PDP-20250204-0047','P-0001','2025-02-04 15:00:00',150000),
('HD-20250205-00000048','PDP-20250205-0048','P-0001','2025-02-05 15:00:00',150000),
('HD-20250206-00000049','PDP-20250206-0049','P-0001','2025-02-06 15:00:00',150000),
('HD-20250207-00000050','PDP-20250207-0050','P-0001','2025-02-07 15:00:00',150000);


GO


-- ===========================
-- TẠO TRIGGERS
-- ===========================

-- 1. Trigger tự động xóa dịch vụ khi xóa ChiTietPhieuDatPhong
CREATE TRIGGER trg_ChiTietPhieuDatPhong_AutoDeleteDichVu
ON ChiTietPhieuDatPhong
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa các dịch vụ liên quan khi xóa chi tiết phiếu đặt phòng
    DELETE ctpdp_dv
    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
    INNER JOIN deleted d ON ctpdp_dv.maPhieuDatPhong = d.maPhieuDatPhong
                        AND ctpdp_dv.maPhong = d.maPhong;
END;
GO

-- 2. Trigger tự động insert dịch vụ vào ChiTietHoaDon_DichVu khi tạo ChiTietHoaDon
CREATE TRIGGER trg_ChiTietHoaDon_AutoInsertDichVu
ON ChiTietHoaDon
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Insert các dịch vụ từ PhieuDatPhong vào ChiTietHoaDon_DichVu
    INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maPhong, maDichVu)
    SELECT DISTINCT
        i.maHoaDon,
        i.maPhieuDatPhong,
        i.maPhong,
        ctpdp_dv.maDichVu
    FROM inserted i
    INNER JOIN ChiTietPhieuDatPhong_DichVu ctpdp_dv 
        ON ctpdp_dv.maPhieuDatPhong = i.maPhieuDatPhong
        AND ctpdp_dv.maPhong = i.maPhong
    WHERE NOT EXISTS (
        SELECT 1 
        FROM ChiTietHoaDon_DichVu cthd_dv
        WHERE cthd_dv.maHoaDon = i.maHoaDon
        AND cthd_dv.maPhieuDatPhong = i.maPhieuDatPhong
        AND cthd_dv.maPhong = i.maPhong
        AND cthd_dv.maDichVu = ctpdp_dv.maDichVu
    );
END;
GO

-- 3. Trigger tự động xóa dịch vụ khi xóa ChiTietHoaDon
CREATE TRIGGER trg_ChiTietHoaDon_AutoDeleteDichVu
ON ChiTietHoaDon
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa các dịch vụ liên quan khi xóa chi tiết hóa đơn
    DELETE cthd_dv
    FROM ChiTietHoaDon_DichVu cthd_dv
    INNER JOIN deleted d ON cthd_dv.maHoaDon = d.maHoaDon
                        AND cthd_dv.maPhieuDatPhong = d.maPhieuDatPhong
                        AND cthd_dv.maPhong = d.maPhong;
END;
GO

-- 4. Trigger cập nhật trạng thái phòng khi thêm PhieuDatPhong
CREATE TRIGGER trg_PhieuDatPhong_UpdateRoomStatus
ON ChiTietPhieuDatPhong
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Cập nhật trạng thái phòng thành "Đã đặt"
    UPDATE p
    SET p.trangThai = N'Đã đặt'
    FROM Phong p
    INNER JOIN inserted i ON p.maPhong = i.maPhong;
END;
GO

-- 5. Trigger cập nhật trạng thái phòng khi hủy/xóa đặt phòng
CREATE TRIGGER trg_ChiTietPhieuDatPhong_RestoreRoomStatus
ON ChiTietPhieuDatPhong
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Khôi phục trạng thái phòng thành "Trống" khi xóa đặt phòng
    UPDATE p
    SET p.trangThai = N'Trống'
    FROM Phong p
    INNER JOIN deleted d ON p.maPhong = d.maPhong;
END;
GO

-- 6. Trigger tự động cập nhật trạng thái phòng khi hủy phòng
CREATE TRIGGER trg_HuyPhong_UpdateRoomStatus
ON HuyPhong
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Cập nhật trạng thái phòng về "Trống" và trạng thái PhieuDatPhong về "Đã hủy"
    UPDATE p
    SET p.trangThai = N'Trống'
    FROM Phong p
    INNER JOIN ChiTietPhieuDatPhong ctpdp ON p.maPhong = ctpdp.maPhong
    INNER JOIN inserted i ON ctpdp.maPhieuDatPhong = i.maPhieuDatPhong;
    
    -- Cập nhật trạng thái PhieuDatPhong
    UPDATE pdp
    SET pdp.trangThai = N'Đã hủy'
    FROM PhieuDatPhong pdp
    INNER JOIN inserted i ON pdp.maPhieuDatPhong = i.maPhieuDatPhong;
    
    -- Cập nhật trạng thái ChiTietPhieuDatPhong
    UPDATE ctpdp
    SET ctpdp.trangThai = N'Đã hủy'
    FROM ChiTietPhieuDatPhong ctpdp
    INNER JOIN inserted i ON ctpdp.maPhieuDatPhong = i.maPhieuDatPhong;
END;
GO
