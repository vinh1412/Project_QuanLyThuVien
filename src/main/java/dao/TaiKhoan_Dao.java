/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.TaiKhoan;
import entity.NhanVien;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class TaiKhoan_Dao {
    private EntityManager em;

    public TaiKhoan_Dao() {
        em= Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    public TaiKhoan getTaiKhoanByTen(String tenDn) {
        return (TaiKhoan) em.createNamedQuery("TaiKhoan.findByTenTaiKhoan").setParameter("tenTaiKhoan", tenDn).getSingleResult();
    }
    public boolean themTaiKhoan(NhanVien nv) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TaiKhoan tk = new TaiKhoan(nv.getSoDienThoai(), "11111111", nv, nv.getChucVu() == 0 ? "BH" : "QL");
            em.persist(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateMatKhau(String tenTaiKhoan, String matKhau) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TaiKhoan tk = em.find(TaiKhoan.class, tenTaiKhoan);
            tk.setMatKhau(matKhau);
            em.merge(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
    public static void main(String[] args) {
        TaiKhoan_Dao tkd = new TaiKhoan_Dao();
        boolean kq=tkd.themTaiKhoan(new NhanVien("NV-0309"));
    }
}
