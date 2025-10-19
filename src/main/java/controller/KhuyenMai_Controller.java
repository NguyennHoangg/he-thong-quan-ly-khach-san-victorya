package controller;

import dao.KhuyenMai_DAO;
import model.KhuyenMai;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KhuyenMai_Controller {

    private final KhuyenMai_DAO dao = new KhuyenMai_DAO();

    public List<KhuyenMai> getAll() {
        return dao.getAll();
    }

    public KhuyenMai findById(String id) {
        if (id == null || id.isBlank())
            return null;
        return dao.findById(id);
    }

    public boolean add(KhuyenMai km) {
        if (!valid(km))
            return false;
        return dao.insert(km);
    }

    public boolean update(KhuyenMai km) {
        if (!valid(km) || km.getMaKhuyenMai() == null || km.getMaKhuyenMai().isBlank())
            return false;
        return dao.update(km);
    }

    public boolean delete(String id) {
        if (id == null || id.isBlank())
            return false;
        return dao.delete(id);
    }

    // --------- quan trọng: gọi đúng tên dao.deleteMany(List<String>) ----------
    public int deleteMany(List<String> ids) {
        if (ids == null || ids.isEmpty())
            return 0;
        List<String> cleaned = ids.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList()); // dùng Collectors để tương thích JDK thấp
        if (cleaned.isEmpty())
            return 0;
        return dao.deleteMany(cleaned);
    }

    private boolean valid(KhuyenMai km) {
        if (km == null)
            return false;
        if (km.getTenKhuyenMai() == null || km.getTenKhuyenMai().isBlank())
            return false;
        if (km.getNgayBatDau() == null || km.getNgayKetThuc() == null)
            return false;
        if (km.getNgayKetThuc().isBefore(km.getNgayBatDau()))
            return false;
        return true;
    }
}
