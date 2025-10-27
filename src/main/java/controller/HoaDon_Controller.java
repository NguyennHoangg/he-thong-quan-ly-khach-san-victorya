/*
 * @ (#) HoaDon_Controller.java     1.0    10/27/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
package controller;


/*
 * @description
 * @author:NguyenTruong
 * @date:  10/27/2025
 * @version:    1.0
 */

import dao.HoaDon_DAO;
import model.HoaDon;

import java.util.List;

public class HoaDon_Controller {
    private HoaDon_DAO hoaDon_dao = new HoaDon_DAO();

    /**
     * Retrieve all HoaDon records via the DAO.
     *
     * @return a List of HoaDon objects (may be empty but never null if DAO follows contract)
     */
    public List<HoaDon> getAllHoaDon(){
        return hoaDon_dao.getAll();
    }

    /**
     * Generate a new unique invoice code (maHoaDon).
     * Format: "HD-YYYYMMDD-NNNNN" where:
     * - YYYYMMDD is the current date
     * - NNNNN is a zero-padded 5-digit random number (00001..10000)
     *
     * @return generated invoice code as String
     */
    public String generateMaHoaDon() {
        // Get today's date
        java.time.LocalDate today = java.time.LocalDate.now();

        // format ngày tháng YYYYMMDD
        String datePart = String.format("%04d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        // tạo 5 số ngẫu nhiên từ 1 tối 10000
        int randomNum = (int) (Math.random() * 10000) + 1;
        String randomPart = String.format("%05d", randomNum);

        // tạo mã cuối
        return "HD-" + datePart + "-" + randomPart;
    }

    public boolean taoHoaDon(HoaDon hoaDon){
        return hoaDon_dao.insertHoaDon(hoaDon);
    }

}
