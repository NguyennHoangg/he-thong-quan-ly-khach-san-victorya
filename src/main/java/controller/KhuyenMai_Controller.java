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
        if (id == null || id.isBlank()) return null;
        return dao.findById(id);
    }
    /**
     * Thêm mới và TRẢ VỀ mã đã gán (mã sinh ở Controller).
     * @return newId hoặc null nếu thất bại.
     */
    public String addAndReturnId(KhuyenMai km) {
        if (!valid(km)) return null;

        String newId = dao.getNextMaKM();
        if (newId == null || newId.isBlank()) return null;


        KhuyenMai kmMoi = new KhuyenMai(
                newId,
                km.getTenKhuyenMai(),
                km.getNgayBatDau(),
                km.getNgayKetThuc(),
                km.isTrangThai(),
                km.getHeSo(),
                km.getTongTienToiThieu(),
                km.getTongKhuyenMaiToiDa()
        );

        boolean ok = dao.insert(kmMoi);
        return ok ? newId : null;
    }


    public boolean add(KhuyenMai km) {
        return addAndReturnId(km) != null;
    }

    public boolean update(KhuyenMai km) {
        if (!valid(km) || km.getMaKhuyenMai() == null || km.getMaKhuyenMai().isBlank())
            return false;
        return dao.update(km);
    }

    public boolean delete(String id) {
        if (id == null || id.isBlank()) return false;
        return dao.delete(id);
    }

    public int deleteMany(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        List<String> cleaned = ids.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (cleaned.isEmpty()) return 0;
        return dao.deleteMany(cleaned);
    }


    private boolean valid(KhuyenMai km) {
        if (km == null) return false;
        if (km.getTenKhuyenMai() == null || km.getTenKhuyenMai().isBlank()) return false;
        if (km.getNgayBatDau() == null || km.getNgayKetThuc() == null) return false;
        if (km.getNgayKetThuc().isBefore(km.getNgayBatDau())) return false;
        return true;
    }

}
