/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.DanhMuc;
import entity.TheLoai;
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
public class TheLoai_Dao {
    private EntityManager em;

    public TheLoai_Dao() {
        em= Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    public boolean themTheLoai(String tenTheLoai, int maDanhMuc) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TheLoai theLoai = new TheLoai();
            theLoai.setTenTheLoai(tenTheLoai);
            DanhMuc danhMuc = em.find(DanhMuc.class, maDanhMuc);
            theLoai.setDanhMuc(danhMuc);
            em.persist(theLoai);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTheLoai(TheLoai theLoai) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(theLoai);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
    public List<TheLoai> timKiemTheLoai(String searchTerm) {
        return em.createNamedQuery("TheLoai.findByTenTheLoai", TheLoai.class).setParameter("tenTheLoai", "%"+searchTerm+"%").getResultList();

    }
    public List<TheLoai> getAllTheLoai() {
        return em.createNamedQuery("TheLoai.findAll", TheLoai.class).getResultList();
    }
    public static void main(String[] args) {
        TheLoai_Dao theLoai_Dao = new TheLoai_Dao();
        List<TheLoai> theLoais = theLoai_Dao.timKiemTheLoai("sách");
        for (TheLoai theLoai: theLoais){
            System.out.println(theLoai.getTenTheLoai());
        }
}
}
