/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectDB.ConnectDB;
import entity.TacGia;
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
public class TacGia_Dao {

    private EntityManager em;

    public TacGia_Dao() {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    public int getThuTuTacGia() {
        return em.createNamedQuery("TacGia.count", Long.class).getSingleResult().intValue();
    }

    public boolean themTacGia(TacGia tg) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(tg);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTacGia(TacGia tg) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(tg);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public List<TacGia> getAllTacGia() {
        return em.createNamedQuery("TacGia.findAll", TacGia.class).getResultList();
    }

    public List<TacGia> timKiemTacGia(String queryParams) {
        return em.createNamedQuery("TacGia.find", TacGia.class)
                .setParameter("maTG", "%" + queryParams + "%")
                .setParameter("tenTG", "%" + queryParams + "%")
                .setParameter("sdt", "%" + queryParams + "%")
                .getResultList();
    }
}
