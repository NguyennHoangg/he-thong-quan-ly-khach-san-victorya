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

import java.util.List;

public class HoaDon_Controller {
    private HoaDon_DAO hoaDon_dao = new HoaDon_DAO();
    public List getAllHoaDon(){
        return hoaDon_dao.getAll();
    }
}
