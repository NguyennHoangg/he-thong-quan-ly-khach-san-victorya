package controller;

import dao.QuanLiPhong_DAO;
import model.LoaiPhong;
import model.Phong;

import java.util.List;

public class QuanLiPhong_Controller {

    private final QuanLiPhong_DAO dao = new QuanLiPhong_DAO();

    public List<LoaiPhong> getAllRoomTypes() {
        return dao.findAllRoomTypes();
    }

    public List<Phong> getAllRooms() {
        return dao.findAll();
    }

    public Phong getRoomById(String maPhong) {
        if (maPhong == null || maPhong.isBlank())
            throw new IllegalArgumentException("Mã phòng không hợp lệ.");
        return dao.findById(maPhong);
    }

    public List<Phong> search(String keyword, String maLoaiPhong, String trangThai, Integer tang) {
        return dao.search(keyword, maLoaiPhong, trangThai, tang);
    }

    public String addRoom(Phong p) {
        validateForCreate(p);

        if (dao.existsBySoPhong(p.getSoPhong()))
            throw new IllegalArgumentException("Số phòng đã tồn tại: " + p.getSoPhong());

        String newId = dao.getNextMaPhong();


        Phong pMoi = new Phong(
                newId,
                p.getSoPhong(),
                p.getLoaiPhong(),
                p.getTrangThai(),
                p.getTang()
        );
        // nếu model có tình trạng thì giữ
        pMoi.setTinhTrang(p.getTinhTrang());

        boolean ok = dao.insert(pMoi);
        if (!ok) throw new IllegalStateException("Thêm phòng thất bại.");

        return newId;
    }

    public boolean updateRoom(Phong p) {
        validateForUpdate(p);

        if (dao.existsBySoPhongExcludingId(p.getSoPhong(), p.getMaPhong()))
            throw new IllegalArgumentException("Số phòng đã tồn tại ở phòng khác: " + p.getSoPhong());

        boolean ok = dao.update(p);
        if (!ok) throw new IllegalStateException("Cập nhật phòng thất bại.");

        return true;
    }

    public boolean deleteRoom(String maPhong) {
        if (maPhong == null || maPhong.isBlank())
            throw new IllegalArgumentException("Mã phòng không hợp lệ.");

        return dao.deleteById(maPhong);
    }

    public int deleteMany(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return dao.deleteMany(ids);
    }

    private void validateForCreate(Phong p) {
        if (p == null)
            throw new IllegalArgumentException("Phòng không được null.");
        if (p.getSoPhong() == null || p.getSoPhong().isBlank())
            throw new IllegalArgumentException("Số phòng không được trống.");
        if (p.getLoaiPhong() == null || p.getLoaiPhong().getMaLoaiPhong() == null
                || p.getLoaiPhong().getMaLoaiPhong().isBlank())
            throw new IllegalArgumentException("Loại phòng không hợp lệ.");
        if (p.getTang() < 0)
            throw new IllegalArgumentException("Tầng không hợp lệ.");
        if (p.getTrangThai() == null || p.getTrangThai().isBlank())
            throw new IllegalArgumentException("Trạng thái không được trống.");
    }

    private void validateForUpdate(Phong p) {
        validateForCreate(p);
        if (p.getMaPhong() == null || p.getMaPhong().isBlank())
            throw new IllegalArgumentException("Mã phòng không được trống khi cập nhật.");
    }
}
