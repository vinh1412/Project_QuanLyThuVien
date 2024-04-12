/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectDB.ConnectDB;
import entity.TacGia;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author PC
 */
public class TacGia_Dao {

    public int getThuTuTacGia() throws SQLException {
        Connection conn;
        conn = ConnectDB.getConnection();
        //Lấy số lượng bản ghi hiện có
        String countQuery = "SELECT COUNT(*) FROM TacGia";
        PreparedStatement countStatement = conn.prepareStatement(countQuery);
        ResultSet countResult = countStatement.executeQuery();
        countResult.next();
        return countResult.getInt(1);
    }

    public boolean themTacGia(TacGia tg) {
        Connection conn = ConnectDB.getConnection();
        String insertQuery = "INSERT INTO TacGia (maTacGia, tenTacGia, soDienThoai, gioiTinh) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement prestm = conn.prepareStatement(insertQuery);
            prestm.setString(1, tg.getMaTacGia());
            prestm.setString(2, tg.getTenTacGia());
            prestm.setString(3, tg.getSoDienThoai());
            prestm.setInt(4, tg.getGioiTinh());

            return (prestm.executeUpdate() > 0);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTacGia(TacGia tg) {
        Connection conn = ConnectDB.getConnection();
        String updateQuery = "UPDATE TacGia SET TenTacGia = ?, SoDienThoai = ?, GioiTinh = ? WHERE MaTacGia = ?";
        try {
            PreparedStatement prestm = conn.prepareStatement(updateQuery);
            prestm.setString(1, tg.getTenTacGia());
            prestm.setString(2, tg.getSoDienThoai());
            prestm.setInt(3, tg.getGioiTinh());
            prestm.setString(4, tg.getMaTacGia());

            return (prestm.executeUpdate() > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<TacGia> getAllTacGia() {
        Connection conn = ConnectDB.getConnection();
        ArrayList<TacGia> danhSachTacGia = new ArrayList<>();
        String selectQuery = "SELECT maTacGia, tenTacGia, soDienThoai, gioiTinh FROM TacGia";

        try {
            Statement stm = conn.createStatement();
            ResultSet result = stm.executeQuery(selectQuery);

            while (result.next()) {
                TacGia tacGia = new TacGia();
                tacGia.setMaTacGia(result.getString("maTacGia"));
                tacGia.setTenTacGia(result.getString("tenTacGia"));
                tacGia.setSoDienThoai(result.getString("soDienThoai"));
                tacGia.setGioiTinh(result.getInt("gioiTinh"));
                danhSachTacGia.add(tacGia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSachTacGia;
    }

    public ArrayList<TacGia> timKiemTacGia(String queryParams) {
        ArrayList<TacGia> danhSachTacGia = new ArrayList<>();
        String query = "SELECT * FROM TacGia WHERE (LOWER(maTacGia)) LIKE LOWER(?) OR [dbo].[RemoveNonASCII](LOWER(tenTacGia)) LIKE LOWER(?) OR (LOWER(tenTacGia)) LIKE LOWER(?) OR soDienThoai LIKE ?";
        Connection conn = ConnectDB.getConnection();

        try {
            PreparedStatement prestm;
            prestm = conn.prepareStatement(query);
            prestm.setString(1, "%" + queryParams + "%"); // Mã tác giả có chứa searchTerm
            prestm.setString(2, "%" + queryParams + "%"); // Tên tác giả có chứa searchTerm
            prestm.setString(3, "%" + queryParams + "%"); // Số điện thoại có chứa searchTerm
            prestm.setString(4, "%" + queryParams + "%"); // Số điện thoại có chứa searchTerm
            try (ResultSet rs = prestm.executeQuery()) {
                while (rs.next()) {
                    String maTacGia = rs.getString("maTacGia");
                    String tenTacGia = rs.getString("tenTacGia");
                    String soDienThoai = rs.getString("soDienThoai");
                    int gioiTinh = rs.getInt("gioiTinh");
                    TacGia tg = new TacGia(maTacGia, tenTacGia, soDienThoai, gioiTinh);
                    danhSachTacGia.add(tg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachTacGia;
    }
}
