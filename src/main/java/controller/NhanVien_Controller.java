package controller;

import java.util.List;

import dao.NhanVien_DAO;
import model.NhanVien;

public class NhanVien_Controller {
    private NhanVien_DAO nv_dao = new NhanVien_DAO();

    public NhanVien_Controller() {
    }

    public  List<NhanVien> getDsNhanVien() {
        return nv_dao.getDsNhanVien();
    }

    public static void main(String[] args) {
        NhanVien_Controller nvc =new NhanVien_Controller();
        List<NhanVien> ds = nvc.getDsNhanVien();
        for(NhanVien nv: ds){
            System.out.println(nv.toString());
        }
    }
}
