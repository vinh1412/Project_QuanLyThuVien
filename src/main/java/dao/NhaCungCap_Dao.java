package dao;

import entity.NhaCungCap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCap_Dao {
    private EntityManager em;

    public NhaCungCap_Dao() {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    public int getThuTuNCC(){
        Long count = em.createNamedQuery("NhaCungCap.countNCC", Long.class).getSingleResult();
        return count.intValue();
    }
    // Thêm nhà cung cấp vào CSDL
    public boolean themNCC(NhaCungCap ncc) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(ncc);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
    // Sửa thông tin nhà xuất bản trong CSDL
    public boolean updateNCC(NhaCungCap ncc) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(ncc);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm nhà xuất bản trong CSDL
    public List<NhaCungCap> timKiemNCC(String searchTerm) {
        return em.createNamedQuery("NhaCungCap.find")
                .setParameter("maNhaCungCap", "%" + searchTerm + "%")
                .setParameter("tenNhaCungCap", "%" + searchTerm + "%")
                .setParameter("diaChi", "%" + searchTerm + "%")
                .setParameter("soDienThoai", "%" + searchTerm + "%")
                .getResultList();
    }

    public List<NhaCungCap> getAllNCC() {
        return em.createNamedQuery("NhaCungCap.findAll", NhaCungCap.class).getResultList();
    }
}
