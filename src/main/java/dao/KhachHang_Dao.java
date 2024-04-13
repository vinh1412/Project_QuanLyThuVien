/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectDB.ConnectDB;
import entity.KhachHang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import utils.GenerateID;
import views.panel_QuanLyKhachHang;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Date;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHang_Dao {

    private EntityManager em;

    public KhachHang_Dao() {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    //them khach hang
    public boolean themKhachHang(KhachHang khachHang) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(khachHang);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public int checkSDT(String sdt) {
        return em.createNamedQuery("KhachHang.checkSDT", Integer.class).setParameter("soDienThoai", sdt).getSingleResult();
    }

    public int getSoLuongKH() {
        Long count = em.createNamedQuery("KhachHang.count", Long.class).getSingleResult();
        return count.intValue();
    }


    public List<KhachHang> getAllKhachHang() {
        return em.createNamedQuery("KhachHang.findAll", KhachHang.class).getResultList();
    }

    public boolean capNhatKH(KhachHang kh) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(kh);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public List<KhachHang> timKhachHang(String thongTin) {
        String thongTinLike = "%" + thongTin.toLowerCase() + "%";
        int gt = -1;
        if (thongTin.equalsIgnoreCase("Nam")) {
            gt = 0;
        } else if (thongTin.equalsIgnoreCase("Nữ")) {
            gt = 1;
        } else if (thongTin.equalsIgnoreCase("Khác") || thongTin.equalsIgnoreCase("Không xác định")) {
            gt = 2;
        }
        TypedQuery<KhachHang> query = em.createNamedQuery("KhachHang.find", KhachHang.class)
                .setParameter("maKH", "%" + thongTinLike+ "%")
                .setParameter("tenKH", "%" + thongTinLike + "%")
                .setParameter("sdt", "%" + thongTinLike + "%");
        if (gt >= 0) { // Chỉ thêm tham số này nếu giới tính được xác định
            query.setParameter("gioiTinh", gt);
        }
        return query.getResultList();
    }

    public KhachHang timKiemKhachHangTheoSDT(String sdt) {
        return em.createNamedQuery("KhachHang.findKHBySDT", KhachHang.class).setParameter("sdt", sdt).getSingleResult();
    }


    //TIM THEO TEN
    public KhachHang getKhachHangByTen(String tenKhachHang) {
        return em.createNamedQuery("KhachHang.findKHByTenKH", KhachHang.class).setParameter("tenKH", tenKhachHang).getSingleResult();
    }
}
