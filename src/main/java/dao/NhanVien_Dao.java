/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.NhanVien;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author PC
 */
public class NhanVien_Dao {
    private EntityManager em;
    public NhanVien_Dao() {
        em= Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    //Them Nhan Vien
    public boolean themNhanVien(NhanVien nhanVien) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(nhanVien);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
        public int checkSDT(String sdt) {
            Long count = em.createNamedQuery("NhanVien.checkSDT", Long.class).setParameter("soDienThoai", sdt).getSingleResult();
            return count.intValue();
        }

    // Lấy số lượng nhan vien
    public int getSoLuongNV() {
        Long count = em.createNamedQuery("NhanVien.countNV", Long.class).getSingleResult();
        return count.intValue();
    }

    //Lay Tat Ca Nhan Vien
    public List<NhanVien> getAllNhanVien() {
        return em.createNamedQuery("NhanVien.findAll", NhanVien.class).getResultList();
    }
    public boolean capNhatNV(NhanVien nv) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(nv);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
    //Tim Kiem
    public List<NhanVien> timNhanVien(String thongTin) {
        return em.createNamedQuery("NhanVien.findNV", NhanVien.class)
                .setParameter("maNhanVien", "%"+thongTin+"%")
                .setParameter("tenNhanVien", "%"+thongTin+"%")
                .setParameter("soDienThoai", "%"+thongTin+"%")
                .setParameter("luongCoBan", Double.parseDouble(thongTin))
                .getResultList();
    }

    //Loc
    public List<NhanVien> LayDanhSachNhanVienTheoTieuChi(int cv, int gt, int tt) {
        List<NhanVien> dsNV=new ArrayList<>();
        String query="SELECT nv FROM NhanVien nv WHERE 1=1";
        if(cv>=0 && cv!=2){
            query+=" AND nv.chucVu = :chucVu";
        }
        if(gt>=0 && gt!=3){
            query+=" AND nv.gioiTinh = :gioiTinh";
        }
        if(tt>=0 && tt!=2){
            query+=" AND nv.trangThai = :trangThai";
        }
        TypedQuery<NhanVien> jpql  = em.createQuery(query, NhanVien.class);
        if (cv >= 0 && cv != 2) {
            jpql.setParameter("chucVu", cv);
        }
        if (gt >= 0 && gt != 3) {
            jpql.setParameter("gioiTinh", gt);
        }
        if (tt >= 0 && tt != 2) {
            jpql.setParameter("trangThai", tt);
        }

        dsNV=jpql.getResultList();
        return dsNV;
    }

    //tim nhan vien theo ma
    public NhanVien timNhanVienByMa(String maNV) {
        return em.createNamedQuery("NhanVien.findNVByMaNV", NhanVien.class).setParameter("maNhanVien", maNV).getSingleResult();
    }
}
