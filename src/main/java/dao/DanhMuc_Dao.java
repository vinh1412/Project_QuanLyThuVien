/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.DanhMuc;
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

/**
 *
 * @author PC
 */
public class DanhMuc_Dao {
    private EntityManager em;

    public DanhMuc_Dao() {
        em= Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    public boolean themDanhMuc(String tenDanhMuc){
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setTenDanhMuc(tenDanhMuc);
            em.persist(danhMuc);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDanhMuc(DanhMuc danhMuc) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(danhMuc);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
    public List<DanhMuc> timKiemDanhMuc(String searchTerm){
        return em.createNamedQuery("DanhMuc.findByTenDanhMuc", DanhMuc.class).setParameter("tenDanhMuc", "%"+searchTerm+"%").getResultList();
    }
    public List<DanhMuc> getAllDanhMuc() {
        return em.createNamedQuery("DanhMuc.findAll", DanhMuc.class).getResultList();
    }
    public void close() {
        em.close();
    }
}
