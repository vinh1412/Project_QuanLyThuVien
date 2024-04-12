/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectDB.ConnectDB;
import entity.KhachHang;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Date;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KhachHang_Dao {

    public KhachHang_Dao() {
    }

    //them khach hang 
    public boolean themKhachHang(KhachHang khachHang) {
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        PreparedStatement psm = null;

        int n = 0;

        try {
            psm = con.prepareStatement("insert into KhachHang (MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh, DiemDoiThuong)  values(?, ?, ?, ?, ?, 0)");
            psm.setString(1, khachHang.getMaKhachHang());
            psm.setString(2, khachHang.getTenKhachHang());
            psm.setInt(3, khachHang.getGioiTinh());
            psm.setString(4, khachHang.getSoDienThoai());
            Date date = khachHang.getNgaySinh();
            java.sql.Date ngaySinh = new java.sql.Date(date.getTime());
            psm.setDate(5, ngaySinh);

            n = psm.executeUpdate();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            try {
                psm.close();
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
        }

        return n > 0;
    }
    
    public int checkSDT(String sdt) {
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        String q = "select count(*) from KhachHang where SoDienThoai = ?";
        int n = 0;
        try {
            stmt = con.prepareStatement(q);
            stmt.setString(1, sdt);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                n = rs.getInt(1);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e2) {
                e2.printStackTrace();
                // TODO: handle exception
            }
        }
        return n;
    }

    public int getSoLuongKH() {
        int soLuong = 0;
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "Select count(*) from KhachHang";
            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                soLuong = rs.getInt(1);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return soLuong;
    }
    
    public KhachHang getKhachHangByMa(String maKhachHang) {
        Connection conn = ConnectDB.getConnection();
        String selectQuery = "SELECT * FROM KhachHang WHERE maKhachHang = ?";
        try {
            PreparedStatement prestm = conn.prepareStatement(selectQuery);
            prestm.setString(1, maKhachHang);

            ResultSet rs = prestm.executeQuery();
            if (rs.next()) {
                
                String tenKH = rs.getString("TenKhachHang");
                int gioiTinh = rs.getInt("GioiTinh");
                int diemDoiThuong = rs.getInt("DiemDoiThuong");
                String soDT = rs.getString("SoDienThoai");
                Date ngaySinh = rs.getDate("NgaySinh");
                // Các thông tin khác của khách hàng

                // Tạo đối tượng KhachHang và trả về
                KhachHang khachHang = new KhachHang(maKhachHang, tenKH, gioiTinh, soDT, ngaySinh,diemDoiThuong);
                return khachHang;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy khách hàng
    }
    

    public ArrayList<KhachHang> getAllKhachHang() {
        ArrayList<KhachHang> dsKH = new ArrayList<KhachHang>();
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();

            String sql = "Select * from KhachHang";
            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                String maKH = rs.getString("MaKhachHang");
                String tenKH = rs.getString("TenKhachHang");
                int gioiTinh = rs.getInt("GioiTinh");
                String soDT = rs.getString("SoDienThoai");
                Date ngaySinh = rs.getDate("NgaySinh");
                int diemDoiThuong = rs.getInt("DiemDoiThuong");

                KhachHang khachHang = new KhachHang(maKH, tenKH, gioiTinh, soDT, ngaySinh, diemDoiThuong);
                dsKH.add(khachHang);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return dsKH;
    }

    public boolean capNhatKH(KhachHang kh) {
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;

        int n = 0;

        try {
            stmt = con.prepareStatement("update KhachHang SET TenKhachHang=?, GioiTinh=?, SoDienThoai=?, NgaySinh=?, DiemDoiThuong=? WHERE MaKhachHang=?");
            stmt.setString(1, kh.getTenKhachHang());
            stmt.setInt(2, kh.getGioiTinh());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setDate(4, new java.sql.Date(kh.getNgaySinh().getTime()));
            stmt.setString(6, kh.getMaKhachHang());
            stmt.setInt(5, kh.getDiemDoiThuong());

            n = stmt.executeUpdate(); //sua duoc thì chuyen thanh 1
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e2) {
                e2.printStackTrace();
                // TODO: handle exception
            }
        }
        return n > 0;
    }

    public ArrayList<KhachHang> timKhachHang(String thongTin) {
        ArrayList<KhachHang> dsKH = new ArrayList<>();

        PreparedStatement stmt = null;

        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM KhachHang WHERE LOWER(MaKhachHang) like LOWER(?) OR [dbo].[RemoveNonASCII](LOWER(TenKhachHang)) like LOWER(?) OR (LOWER(TenKhachHang)) like LOWER(?) OR GioiTinh = ? OR SoDienThoai like ? OR LOWER(SoDienThoai) = LOWER(?)";
//                                OR NgaySinh LIKE ?

            stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + thongTin + "%");
            stmt.setString(2, "%" + thongTin + "%");
            stmt.setString(3, "%" + thongTin + "%");

            int gt;
            if (thongTin.equalsIgnoreCase("Nam")) {
                gt = 0;
            } else if (thongTin.equalsIgnoreCase("Nữ")) {
                gt = 1;
            } else if (thongTin.equalsIgnoreCase("Khác") || thongTin.equalsIgnoreCase("Không xác định")) {
                gt = 2;
            } else {
                gt = -1;
            }
            stmt.setInt(4, gt);
            stmt.setString(5, "%" + thongTin + "%");
            stmt.setString(6, "%" + thongTin + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String maKH = rs.getString("MaKhachHang");
                String tenKH = rs.getString("TenKhachHang");
                int gioiTinh = rs.getInt("GioiTinh");
                String soDT = rs.getString("SoDienThoai");
                Date ngaySinh = rs.getDate("NgaySinh");
                int diemDoiThuong = rs.getInt("DiemDoiThuong");

                KhachHang kh = new KhachHang(maKH, tenKH, gioiTinh, soDT, ngaySinh, diemDoiThuong);
                dsKH.add(kh);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e2) {
                // TODO: handle exception
                e2.printStackTrace();
            }
        }
        return dsKH;

    }

    public KhachHang timKiemKhachHangTheoSDT(String sdt) {
        Connection conn = ConnectDB.getConnection();
        String selectQuery = "SELECT * FROM KhachHang WHERE SoDienThoai = ?";
        KhachHang khachHang = null;

        try {
            PreparedStatement prestm = conn.prepareStatement(selectQuery);
            prestm.setString(1, sdt);

            ResultSet rs = prestm.executeQuery();
            if (rs.next()) {
                String maKhachHangResult = rs.getString("MaKhachHang");
                String tenKhachHangResult = rs.getString("TenKhachHang");
                int gioiTinh = rs.getInt("GioiTinh");
                Date ngaySinh = rs.getDate("NgaySinh");
                int diemDoiThuong = rs.getInt("DiemDoiThuong");

                khachHang = new KhachHang(maKhachHangResult, tenKhachHangResult, gioiTinh, sdt, ngaySinh, diemDoiThuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return khachHang;
    }
    
    
      //TIM THEO TEN
      public KhachHang getKhachHangByTen(String tenKhachHang) {
        Connection conn = ConnectDB.getConnection();
        String selectQuery = "SELECT * FROM KhachHang WHERE TenKhachHang = ?";
        try {
            PreparedStatement prestm = conn.prepareStatement(selectQuery);
            prestm.setString(1, tenKhachHang);

            ResultSet rs = prestm.executeQuery();
            if (rs.next()) {
                String maKhachHang = rs.getString("MaKhachHang");
                String tenKH = rs.getString("TenKhachHang");
                int gioiTinh = rs.getInt("GioiTinh");
                int diemDoiThuong = rs.getInt("DiemDoiThuong");
                String soDT = rs.getString("SoDienThoai");
                Date ngaySinh = rs.getDate("NgaySinh");
                // Các thông tin khác của khách hàng

                // Tạo đối tượng KhachHang và trả về
                KhachHang khachHang = new KhachHang(maKhachHang, tenKH, gioiTinh, soDT, ngaySinh,diemDoiThuong);
                return khachHang;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy khách hàng
    }

}
