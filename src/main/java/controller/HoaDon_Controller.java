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


        public List<HoaDon> getAllHoaDon() {

            return hoaDon_dao.getAll();
        }

        public String generateMaHoaDon() {
            java.time.LocalDate today = java.time.LocalDate.now();
            String datePart = String.format("%04d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth());

            // tạo 5 số ngẫu nhiên từ 1 tối 10000
            int randomNum = (int) (Math.random() * 10000) + 1;
            String randomPart = String.format("%05d", randomNum);

            // tạo mã cuối
            return "HD-" + datePart + "-" + randomPart;
        }



    }
