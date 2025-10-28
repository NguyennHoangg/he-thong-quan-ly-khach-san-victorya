-- Tạo database
CREATE DATABASE Victorya_Hotel;
GO

USE Victorya_Hotel;
GO

-- 1. TaiKhoan
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(100) NOT NULL,
    vaiTro NVARCHAR (50) NOT NULL
);

-- 2. NhanVien
CREATE TABLE NhanVien (
    maNhanVien VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(12),
    tenNhanVien NVARCHAR (100),
    gioiTinh BIT,
    ngaySinh DATE,
    email VARCHAR(100),
    soDienThoai VARCHAR(20),
    ngayBatDau DATE,
    tenDangNhap VARCHAR(50),
    trangThai NVARCHAR (50),
    diaChi NVARCHAR(50),
    FOREIGN KEY (tenDangNhap) REFERENCES TaiKhoan (tenDangNhap)
);

-- 3. Ca
CREATE TABLE Ca (
    maCa VARCHAR(20) PRIMARY KEY,
    ngayBatDau DATE,
    ngayKetThuc DATE
);

-- 4. CaLamViecNhanVien
CREATE TABLE CaLamViecNhanVien (
    maCaLamViec VARCHAR(20) PRIMARY KEY,
    maNhanVien VARCHAR(20),
    ngay DATE,
    tienMoCa FLOAT,
    tienKetCa FLOAT,
    maCa VARCHAR(20),
    trangThai NVARCHAR (50),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien (maNhanVien),
    FOREIGN KEY (maCa) REFERENCES Ca (maCa)
);

-- 5. LoaiPhong
CREATE TABLE LoaiPhong (
    maLoaiPhong VARCHAR(20) PRIMARY KEY,
    tenLoaiPhong NVARCHAR (100),
    gia DECIMAL(18, 2),
    ngayTao DATE
);

-- 6. Phong
CREATE TABLE Phong (
    maPhong VARCHAR(20) PRIMARY KEY,
    soPhong NVARCHAR (100),
    trangThai NVARCHAR (50),
    maLoaiPhong VARCHAR(20),
    tang int,
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong (maLoaiPhong)
);

-- 7. LoaiDatPhong
CREATE TABLE LoaiDatPhong (
    maLoaiDatPhong VARCHAR(20) PRIMARY KEY,
    tenLoaiDatPhong NVARCHAR (100),
    ngayTao DATE
);

-- 8. DichVu
CREATE TABLE DichVu (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR (100),
    gia DECIMAL(18, 2),
    moTa NVARCHAR (255),
    donViTinh NVARCHAR (50)
);

CREATE TABLE DichVu_LoaiPhong (
    maDichVu VARCHAR(20),
    maLoaiPhong VARCHAR(20),
    PRIMARY KEY (maLoaiPhong, maDichVu),
    FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong (maLoaiPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu (maDichVu)
)

-- 9. KhachHang
CREATE TABLE KhachHang (
    maKhachHang VARCHAR(20) PRIMARY KEY,
    CCCD VARCHAR(20) UNIQUE NOT NULL,
    hoTen NVARCHAR (100),
    soDienThoai VARCHAR(20),
    email VARCHAR(100),
    ngayTao DATE
);

-- 10. PhieuDatPhong
CREATE TABLE PhieuDatPhong (
    maPhieuDatPhong VARCHAR(20) PRIMARY KEY,
    ngayTao DATE,
    maKhachHang VARCHAR(20),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang (maKhachHang)
);

-- 11. ChiTietPhieuDatPhong
CREATE TABLE ChiTietPhieuDatPhong (
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    thoiGianNhanPhong DATETIME,
    thoiGianTraPhong DATETIME,
    maLoaiDatPhong VARCHAR(20),
    soNguoi int,
    PRIMARY KEY (maPhieuDatPhong, maPhong),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong (maPhieuDatPhong),
    FOREIGN KEY (maPhong) REFERENCES Phong (maPhong),
    FOREIGN KEY (maLoaiDatPhong) REFERENCES LoaiDatPhong (maLoaiDatPhong)
);

-- 12. KhuyenMai
CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(20) PRIMARY KEY,
    tenKhuyenMai NVARCHAR (100),
    ngayBatDau DATE,
    ngayKetThuc DATE,
    trangThai NVARCHAR (50),
    heSo FLOAT,
    tongTienToiThieu DECIMAL(18, 2),
    tongKhuyenMaiToiDa DECIMAL(18, 2)
);

-- 13. HoaDon
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(20) PRIMARY KEY,
    ngayDat DATETIME,
    maKhachHang VARCHAR(20),
    maNhanVien VARCHAR(20),
    maKhuyenMai VARCHAR(20),
    ngayTao DATE,
    trangThai NVARCHAR (50),
    tongTien DECIMAL(18, 2),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang (maKhachHang),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien (maNhanVien),
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai (maKhuyenMai)
);

-- 14. ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(20),
    maPhieuDatPhong VARCHAR(20),
    ngayTao DATE,
    tongTien DECIMAL(18, 2) DEFAULT 0,
    PRIMARY KEY (maHoaDon, maPhieuDatPhong),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon (maHoaDon),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong (maPhieuDatPhong)
);

CREATE TABLE ChiTietPhieuDatPhong_DichVu (
    maPhieuDatPhong VARCHAR(20),
    maPhong VARCHAR(20),
    maDichVu VARCHAR(20),
    soLuong INT,
    PRIMARY KEY (
        maPhieuDatPhong,
        maPhong,
        maDichVu
    ),
    FOREIGN KEY (maPhieuDatPhong, maPhong) REFERENCES ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu (maDichVu)
);

CREATE TABLE ChiTietHoaDon_DichVu (
    maHoaDon VARCHAR(20),
    maPhieuDatPhong VARCHAR(20),
    maDichVu VARCHAR(20),
    PRIMARY KEY (
        maHoaDon,
        maPhieuDatPhong,
        maDichVu
    ),
    FOREIGN KEY (maHoaDon, maPhieuDatPhong) REFERENCES ChiTietHoaDon (maHoaDon, maPhieuDatPhong),
    FOREIGN KEY (maDichVu) REFERENCES DichVu (maDichVu)
);

-- 15. HuyPhong
CREATE TABLE HuyPhong (
    maHuyPhong INT IDENTITY PRIMARY KEY,
    maPhieuDatPhong VARCHAR(20) NOT NULL,
    lyDo NVARCHAR (255),
    ngayHuy DATETIME DEFAULT GETDATE (),
    FOREIGN KEY (maPhieuDatPhong) REFERENCES PhieuDatPhong (maPhieuDatPhong)
);

-- 16. DanhGia (Bảng phản hồi khách hàng)
CREATE TABLE DanhGia (
    maDanhGia INT IDENTITY PRIMARY KEY,
    maKhachHang VARCHAR(20) NOT NULL,
    maPhong VARCHAR(20),
    noiDung NVARCHAR (1000),
    ngayTao DATETIME DEFAULT GETDATE (),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang (maKhachHang),
    FOREIGN KEY (maPhong) REFERENCES Phong (maPhong)
);

-- ===========================
-- TẠO CÁC INDEX ĐỂ TỐI ƯU HIỆU SUẤT
-- ===========================

-- Index cho các khóa ngoại thường được tìm kiếm
CREATE INDEX IX_NhanVien_TenDangNhap ON NhanVien (tenDangNhap);

CREATE INDEX IX_CaLamViecNhanVien_MaNhanVien ON CaLamViecNhanVien (maNhanVien);

CREATE INDEX IX_CaLamViecNhanVien_MaCa ON CaLamViecNhanVien (maCa);

CREATE INDEX IX_CaLamViecNhanVien_Ngay ON CaLamViecNhanVien (ngay);

-- Index cho bảng Phong
CREATE INDEX IX_Phong_MaLoaiPhong ON Phong (maLoaiPhong);

CREATE INDEX IX_Phong_TrangThai ON Phong (trangThai);

-- Index cho PhieuDatPhong
CREATE INDEX IX_PhieuDatPhong_MaKhachHang ON PhieuDatPhong (maKhachHang);

CREATE INDEX IX_KhachHang_CCCD ON KhachHang (CCCD);

CREATE INDEX IX_PhieuDatPhong_NgayTao ON PhieuDatPhong (ngayTao);

-- Index cho ChiTietPhieuDatPhong
CREATE INDEX IX_ChiTietPhieuDatPhong_MaPhieuDatPhong ON ChiTietPhieuDatPhong (maPhieuDatPhong);

CREATE INDEX IX_ChiTietPhieuDatPhong_MaPhong ON ChiTietPhieuDatPhong (maPhong);

CREATE INDEX IX_ChiTietPhieuDatPhong_thoiGianNhanPhong ON ChiTietPhieuDatPhong (thoiGianNhanPhong);

CREATE INDEX IX_ChiTietPhieuDatPhong_thoiGianTraPhong ON ChiTietPhieuDatPhong (thoiGianTraPhong);
-- Index for DichVu on junction table
CREATE INDEX IX_ChiTietPhieuDatPhong_DichVu_MaDichVu ON ChiTietPhieuDatPhong_DichVu (maDichVu);

-- Index cho KhuyenMai
CREATE INDEX IX_KhuyenMai_NgayBatDau ON KhuyenMai (ngayBatDau);

CREATE INDEX IX_KhuyenMai_NgayKetThuc ON KhuyenMai (ngayKetThuc);

CREATE INDEX IX_KhuyenMai_TrangThai ON KhuyenMai (trangThai);

-- Index cho HoaDon
CREATE INDEX IX_HoaDon_MaKhachHang ON HoaDon (maKhachHang);

CREATE INDEX IX_HoaDon_MaNhanVien ON HoaDon (maNhanVien);

CREATE INDEX IX_HoaDon_NgayDat ON HoaDon (ngayDat);

CREATE INDEX IX_HoaDon_NgayTao ON HoaDon (ngayTao);

CREATE INDEX IX_HoaDon_TrangThai ON HoaDon (trangThai);

CREATE INDEX IX_HoaDon_MaKhuyenMai ON HoaDon (maKhuyenMai);

-- Index cho ChiTietHoaDon
CREATE INDEX IX_ChiTietHoaDon_maHoaDon ON ChiTietHoaDon (maHoaDon);

CREATE INDEX IX_ChiTietHoaDon_DichVu_MaDichVu ON ChiTietHoaDon_DichVu (maDichVu);

CREATE INDEX IX_ChiTietHoaDon_MaPhieuDatPhong ON ChiTietHoaDon (maPhieuDatPhong);

CREATE INDEX IX_ChiTietHoaDon_NgayTao ON ChiTietHoaDon (ngayTao);

-- Index cho KhachHang
CREATE INDEX IX_KhachHang_SoDienThoai ON KhachHang (soDienThoai);

CREATE INDEX IX_KhachHang_Email ON KhachHang (email);

CREATE INDEX IX_KhachHang_NgayTao ON KhachHang (ngayTao);

-- Index cho HuyPhong
CREATE INDEX IX_HuyPhong_MaPhieuDatPhong ON HuyPhong (maPhieuDatPhong);

CREATE INDEX IX_HuyPhong_NgayHuy ON HuyPhong (ngayHuy);

-- Index cho DanhGia
CREATE INDEX IX_DanhGia_MaKhachHang ON DanhGia (maKhachHang);

CREATE INDEX IX_DanhGia_MaPhong ON DanhGia (maPhong);

CREATE INDEX IX_DanhGia_NgayTao ON DanhGia (ngayTao);

-- Index kết hợp cho các truy vấn phức tạp
CREATE INDEX IX_ChiTietPhieuDatPhong_PhongVaTime ON ChiTietPhieuDatPhong (
    maPhong,
    thoiGianNhanPhong,
    thoiGianTraPhong
);

CREATE INDEX IX_HoaDon_KhachHangVaNgay ON HoaDon (maKhachHang, ngayTao);

CREATE INDEX IX_CaLamViecNhanVien_NhanVienVaNgay ON CaLamViecNhanVien (maNhanVien, ngay);

-- ===========================
-- THÊM CÁC CONSTRAINT BỔ SUNG
-- ===========================

-- Constraint cho email format
ALTER TABLE KhachHang
ADD CONSTRAINT CK_KhachHang_Email CHECK (email LIKE '%@%.%');

ALTER TABLE NhanVien
ADD CONSTRAINT CK_NhanVien_Email CHECK (
    email LIKE '%@%.%'
    OR email IS NULL
);

-- Constraint cho số điện thoại
ALTER TABLE KhachHang
ADD CONSTRAINT CK_KhachHang_SoDienThoai CHECK (
    LEN (soDienThoai) >= 10
    AND soDienThoai NOT LIKE '%[^0-9]%'
);

ALTER TABLE NhanVien
ADD CONSTRAINT CK_NhanVien_SoDienThoai CHECK (
    LEN (soDienThoai) >= 10
    AND soDienThoai NOT LIKE '%[^0-9]%'
    OR soDienThoai IS NULL
);

-- Constraint cho giờ check-in/check-out
ALTER TABLE ChiTietPhieuDatPhong
ADD CONSTRAINT CK_ChiTietPhieuDatPhong_Time CHECK (
    thoiGianTraPhong > thoiGianNhanPhong
);

-- Constraint cho ngày khuyến mãi
ALTER TABLE KhuyenMai
ADD CONSTRAINT CK_KhuyenMai_NgayApDung CHECK (ngayKetThuc >= ngayBatDau);

-- Constraint cho hệ số khuyến mãi
ALTER TABLE KhuyenMai
ADD CONSTRAINT CK_KhuyenMai_HeSo CHECK (
    heSo >= 0
    AND heSo <= 1
);

-- ===========================
-- STORED PROCEDURES CHO BACKUP/RESTORE
-- ===========================

-- Procedure backup database
GO
CREATE PROCEDURE sp_BackupDatabase
    @BackupPath NVARCHAR(500),
    @BackupName NVARCHAR(200) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @DatabaseName NVARCHAR(128) = DB_NAME();
    DECLARE @FileName NVARCHAR(500);
    DECLARE @BackupCommand NVARCHAR(1000);
    
    -- Tạo tên file backup nếu không được cung cấp
    IF @BackupName IS NULL
        SET @BackupName = @DatabaseName + '_' + FORMAT(GETDATE(), 'yyyyMMdd_HHmmss');
    
    -- Tạo đường dẫn file backup đầy đủ
    SET @FileName = @BackupPath + '\' + @BackupName + '.bak';
    
    -- Tạo lệnh backup
    SET @BackupCommand = 'BACKUP DATABASE [' + @DatabaseName + '] TO DISK = ''' + @FileName + ''' 
        WITH FORMAT, INIT, COMPRESSION, 
        NAME = ''' + @BackupName + ''',
        DESCRIPTION = ''Backup created on ' + CONVERT(VARCHAR, GETDATE(), 120) + ''';';
    
    -- Thực hiện backup
    EXEC sp_executesql @BackupCommand;
    
    SELECT 
        'SUCCESS' AS Status,
        @FileName AS BackupFile,
        GETDATE() AS BackupTime,
        @DatabaseName AS DatabaseName;
END;

-- Procedure restore database
GO
CREATE PROCEDURE sp_RestoreDatabase
    @BackupPath NVARCHAR(500),
    @TargetDatabaseName NVARCHAR(128) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @CurrentDatabaseName NVARCHAR(128) = DB_NAME();
    DECLARE @RestoreCommand NVARCHAR(2000);
    
    -- Sử dụng tên database hiện tại nếu không được cung cấp
    IF @TargetDatabaseName IS NULL
        SET @TargetDatabaseName = @CurrentDatabaseName;
    
    -- Tạo lệnh restore
    SET @RestoreCommand = 'RESTORE DATABASE [' + @TargetDatabaseName + '] 
        FROM DISK = ''' + @BackupPath + ''' 
        WITH REPLACE, RECOVERY;';
    
    -- Thực hiện restore
    EXEC sp_executesql @RestoreCommand;
    
    SELECT 
        'SUCCESS' AS Status,
        @BackupPath AS RestoreFile,
        GETDATE() AS RestoreTime,
        @TargetDatabaseName AS DatabaseName;
END;

-- Procedure liệt kê các file backup
GO
CREATE PROCEDURE sp_ListBackupFiles
    @BackupDirectory NVARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Command NVARCHAR(1000);
    
    -- Tạo bảng tạm để chứa kết quả
    CREATE TABLE #BackupFiles (
        FileName NVARCHAR(500),
        Depth INT,
        IsFile BIT
    );
    
    -- Lệnh để liệt kê files
    SET @Command = 'DIR "' + @BackupDirectory + '\*.bak" /B';
    
    -- Thực hiện lệnh và lưu kết quả
    INSERT INTO #BackupFiles
    EXEC xp_dirtree @BackupDirectory, 1, 1;
    
    -- Trả về danh sách file backup
    SELECT 
        FileName,
        @BackupDirectory + '\' + FileName AS FullPath
    FROM #BackupFiles 
    WHERE IsFile = 1 AND FileName LIKE '%.bak'
    ORDER BY FileName DESC;
    
    DROP TABLE #BackupFiles;
END;

-- ===========================
-- TRIGGERS TỰ ĐỘNG
-- ===========================

-- 1. Trigger tự động xóa khỏi ChiTietPhieuDatPhong_DichVu khi xóa ChiTietPhieuDatPhong
GO
CREATE TRIGGER trg_ChiTietPhieuDatPhong_AutoDeleteDichVu
ON ChiTietPhieuDatPhong
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa khỏi bảng phụ khi xóa chi tiết phiếu đặt phòng
    DELETE ctpdp_dv
    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
    INNER JOIN deleted d ON ctpdp_dv.maPhieuDatPhong = d.maPhieuDatPhong
                        AND ctpdp_dv.maPhong = d.maPhong;
END;

-- 2. Trigger tự động insert vào ChiTietHoaDon_DichVu khi tạo ChiTietHoaDon
GO
CREATE TRIGGER trg_ChiTietHoaDon_AutoInsertDichVu
ON ChiTietHoaDon
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Insert các dịch vụ từ PhieuDatPhong vào ChiTietHoaDon_DichVu
    INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maDichVu)
    SELECT DISTINCT
        i.maHoaDon,
        i.maPhieuDatPhong,
        ctpdp_dv.maDichVu
    FROM inserted i
    INNER JOIN ChiTietPhieuDatPhong_DichVu ctpdp_dv 
        ON ctpdp_dv.maPhieuDatPhong = i.maPhieuDatPhong
    WHERE NOT EXISTS (
        SELECT 1 
        FROM ChiTietHoaDon_DichVu cthd_dv
        WHERE cthd_dv.maHoaDon = i.maHoaDon
        AND cthd_dv.maPhieuDatPhong = i.maPhieuDatPhong
        AND cthd_dv.maDichVu = ctpdp_dv.maDichVu
    );
END;

-- 3. Trigger tự động xóa khỏi ChiTietHoaDon_DichVu khi xóa ChiTietHoaDon
GO
CREATE TRIGGER trg_ChiTietHoaDon_AutoDeleteDichVu
ON ChiTietHoaDon
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xóa khỏi bảng phụ khi xóa chi tiết hóa đơn
    DELETE cthd_dv
    FROM ChiTietHoaDon_DichVu cthd_dv
    INNER JOIN deleted d ON cthd_dv.maHoaDon = d.maHoaDon
                        AND cthd_dv.maPhieuDatPhong = d.maPhieuDatPhong;
END;

GO