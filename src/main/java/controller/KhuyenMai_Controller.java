package controller;

import dao.KhuyenMai_DAO;
import model.KhuyenMai;

import java.util.List;

public class KhuyenMai_Controller {
    private final KhuyenMai_DAO dao = new KhuyenMai_DAO();

    public List<KhuyenMai> getAll() {
        return dao.getAll();
    }

    public KhuyenMai getById(String id) {
        return dao.findById(id);
    }

    public boolean add(KhuyenMai km) {
        return dao.insert(km);
    }

    public boolean update(KhuyenMai km) {
        return dao.update(km);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }
}
