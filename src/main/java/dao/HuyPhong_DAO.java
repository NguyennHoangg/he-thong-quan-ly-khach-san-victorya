package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import config.ConnectDatabase;
import model.ChiTietPhieuDatPhong;

public class HuyPhong_DAO {
    public HuyPhong_DAO() {
    }

    public boolean themHuyPhong(List<ChiTietPhieuDatPhong> dsPhieuDatPhong, String lyDo, LocalDate ngayHuy) {
        String sql = "INSERT INTO HuyPhong(maPhieuDatPhong, lyDo, ngayHuy) VALUES (?, ?, ?)";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement stmt = connect.prepareStatement(sql)) {

            // Duyệt danh sách các chi tiết phiếu đặt phòng
            for (ChiTietPhieuDatPhong ct : dsPhieuDatPhong) {
                if (ct.getPhieuDatPhong() == null || ct.getPhieuDatPhong().getMaPhieuDatPhong() == null)
                    continue; // bỏ qua dòng null

                stmt.setString(1, ct.getPhieuDatPhong().getMaPhieuDatPhong());
                stmt.setString(2, lyDo);
                stmt.setDate(3, Date.valueOf(ngayHuy));

                stmt.addBatch(); // gom lệnh vào batch chưa thực thi insert
            }

            // Thực thi batch (một lần gửi nhiều câu lệnh đến DB)
            int[] result = stmt.executeBatch();

            connect.close();
            return result.length > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
