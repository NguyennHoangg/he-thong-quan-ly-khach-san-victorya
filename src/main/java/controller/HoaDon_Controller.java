/*
 * @ (#) HoaDon_Controller.java     1.1    12/01/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package controller;

/*
 * @description  Controller cho nghiệp vụ Hóa đơn
 * @author:     NguyenTruong
 * @date:       10/27/2025
 * @version:    1.1
 */

import dao.HoaDon_DAO;
import model.HoaDon;

import java.time.LocalDate;
import java.util.List;

public class HoaDon_Controller {

    private final HoaDon_DAO hoaDon_dao = new HoaDon_DAO();


    public List<HoaDon> getAllHoaDon() {
        return hoaDon_dao.getAll();
    }


    public List<HoaDon> timKiemHoaDon(String tuKhoa,
                                      String trangThaiRaw,
                                      LocalDate tuNgay,
                                      LocalDate denNgay) {
        return hoaDon_dao.timKiem(tuKhoa, trangThaiRaw, tuNgay, denNgay);
    }


    public String generateMaHoaDon() {
        LocalDate today = LocalDate.now();
        String datePart = String.format(
                "%04d%02d%02d",
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth()
        );


        int randomNum = (int) (Math.random() * 10000) + 1;
        String randomPart = String.format("%05d", randomNum);


        return "HD-" + datePart + "-" + randomPart;
    }
}
