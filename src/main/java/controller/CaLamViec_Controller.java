package controller;

import dao.CaLamViecNhanVien_DAO;
import model.CaLamViecNhanVien;

public class CaLamViec_Controller {
    private CaLamViecNhanVien_DAO ca = new CaLamViecNhanVien_DAO();

    public boolean moCaLamViec(CaLamViecNhanVien caLamViecNhanVien){
        return ca.moCaLamViec(null, 0);
    }
}
