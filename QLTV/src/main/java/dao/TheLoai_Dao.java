/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import bus.TheLoai_Bus;
import entity.DanhMuc;
import entity.TheLoai;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class TheLoai_Dao extends UnicastRemoteObject implements TheLoai_Bus {
    private EntityManager em;

    public TheLoai_Dao() throws RemoteException {
        em= Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    @Override
    public boolean themTheLoai(String tenTheLoai, int maDanhMuc) throws RemoteException{
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

    @Override
    public boolean updateTheLoai(TheLoai theLoai) throws RemoteException{
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
    @Override
    public List<TheLoai> timKiemTheLoai(String queryParams) throws RemoteException{
        return em.createNamedQuery("TheLoai.findByTenTheLoai", TheLoai.class).setParameter("tenTheLoai", "%"+queryParams+"%").getResultList();

    }
    @Override
    public List<TheLoai> getAllTheLoai() throws RemoteException{
        return em.createNamedQuery("TheLoai.findAll", TheLoai.class).getResultList();
    }
}
