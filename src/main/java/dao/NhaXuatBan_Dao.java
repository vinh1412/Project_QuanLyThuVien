/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectDB.ConnectDB;
import entity.NhaXuatBan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author PC
 */
public class NhaXuatBan_Dao {

    private EntityManager em;
    public NhaXuatBan_Dao() {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    public int getThuTuNXB() {
        Long count = em.createNamedQuery("NhaXuatBan.count", Long.class).getSingleResult();
        return count.intValue();
    }
    // Thêm nhà xuất bản vào CSDL

    public boolean themNXB(NhaXuatBan nxb) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(nxb);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    // Sửa thông tin nhà xuất bản trong CSDL
    public boolean updateNXB(NhaXuatBan nxb) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(nxb);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm nhà xuất bản trong CSDL
    public List<NhaXuatBan> timKiemNXB(String searchTerm) {
        return  em.createNamedQuery("NhaXuatBan.find", NhaXuatBan.class)
                .setParameter("maNXB", "%" + searchTerm + "%")
                .setParameter("tenNXB", "%" + searchTerm + "%")
                .setParameter("sdt", "%" + searchTerm + "%")
                .getResultList();
    }

    public List<NhaXuatBan> getAllNXB() {
        return em.createNamedQuery("NhaXuatBan.findAll", NhaXuatBan.class).getResultList();
    }

}
