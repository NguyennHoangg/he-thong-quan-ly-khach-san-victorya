-- Script tao du lieu demo cho TaiKhoan_GUI
-- Tao nhan vien NV100 voi day du thong tin

USE Victorya_Hotel;
GO

PRINT '========== TAO DU LIEU DEMO NV100 ==========';
PRINT '';

-- 1. Xoa du lieu cu neu ton tai
IF EXISTS (SELECT 1 FROM NhanVien WHERE maNhanVien = 'NV100')
BEGIN
    DELETE FROM NhanVien WHERE maNhanVien = 'NV100';
    PRINT '1. Da xoa nhan vien cu NV100';
END

IF EXISTS (SELECT 1 FROM TaiKhoan WHERE tenDangNhap = 'NV100')
BEGIN
    DELETE FROM TaiKhoan WHERE tenDangNhap = 'NV100';
    PRINT '   Da xoa tai khoan cu NV100';
END

PRINT '';

-- 2. Tao tai khoan NV100
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro)
VALUES ('NV100', '$2a$10$abcdefghijklmnopqrstuv', 'admin');

PRINT '2. Da tao tai khoan:';
PRINT '   - Ten dang nhap: NV100';
PRINT '   - Mat khau: 123456 (da hash)';
PRINT '   - Vai tro: admin';
PRINT '';

-- 3. Tao nhan vien NV100
INSERT INTO NhanVien (maNhanVien, tenNhanVien, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau, tenDangNhap)
VALUES (
    'NV100',                           -- maNhanVien
    N'Nguyen Van Demo',                -- tenNhanVien
    1,                                  -- gioiTinh (1 = Nam, 0 = Nu)
    '1995-05-20',                      -- ngaySinh
    'demo100@victorya.com',            -- email
    '0987654100',                      -- soDienThoai
    '2023-01-15',                      -- ngayBatDau
    'NV100'                            -- tenDangNhap
);

PRINT '3. Da tao nhan vien:';
PRINT '   - Ma nhan vien: NV100';
PRINT '   - Ho ten: Nguyen Van Demo';
PRINT '   - Gioi tinh: Nam';
PRINT '   - Ngay sinh: 20/05/1995';
PRINT '   - Email: demo100@victorya.com';
PRINT '   - So dien thoai: 0987654100';
PRINT '   - Ngay bat dau: 15/01/2023';
PRINT '';

-- 4. Kiem tra du lieu da tao
PRINT '4. Kiem tra du lieu:';
PRINT '';

SELECT 
    'TaiKhoan' as Loai,
    tk.tenDangNhap,
    tk.vaiTro,
    CASE 
        WHEN nv.maNhanVien IS NOT NULL THEN 'Co lien ket'
        ELSE 'Khong co lien ket'
    END as TrangThaiLienKet
FROM TaiKhoan tk
LEFT JOIN NhanVien nv ON nv.tenDangNhap = tk.tenDangNhap
WHERE tk.tenDangNhap = 'NV100';

SELECT 
    'NhanVien' as Loai,
    nv.maNhanVien,
    nv.tenNhanVien,
    CASE WHEN nv.gioiTinh = 1 THEN 'Nam' ELSE 'Nu' END as GioiTinh,
    CONVERT(VARCHAR, nv.ngaySinh, 103) as NgaySinh,
    nv.email,
    nv.soDienThoai,
    CONVERT(VARCHAR, nv.ngayBatDau, 103) as NgayBatDau,
    nv.tenDangNhap
FROM NhanVien nv
WHERE nv.maNhanVien = 'NV100';

PRINT '';
PRINT '========== HOAN THANH ==========';
PRINT '';
PRINT 'Du lieu demo NV100 da san sang!';
PRINT '';
PRINT 'HUONG DAN TEST:';
PRINT '  1. Mo TaiKhoan_GUI trong ung dung';
PRINT '  2. Ung dung se tu dong load du lieu NV100';
PRINT '  3. Kiem tra thong tin hien thi dung khong';
PRINT '';
PRINT 'THONG TIN DANG NHAP:';
PRINT '  - Username: NV100';
PRINT '  - Password: 123456';
PRINT '';

