/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import bus.DanhMuc_Bus;
import entity.DanhMuc;
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

/**
 *
 * @author PC
 */
public class DanhMuc_Dao extends UnicastRemoteObject implements DanhMuc_Bus {
    private EntityManager em;

    public DanhMuc_Dao() throws RemoteException {
        em= Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    @Override
    public boolean themDanhMuc(String tenDanhMuc) throws RemoteException{
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
    @Override
    public boolean updateDanhMuc(DanhMuc danhMuc) throws RemoteException{
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
    @Override
    public List<DanhMuc> timKiemDanhMuc(String searchTerm) throws RemoteException{
        return em.createNamedQuery("DanhMuc.findByTenDanhMuc", DanhMuc.class).setParameter("tenDanhMuc", "%"+searchTerm+"%").getResultList();
    }
    @Override
    public List<DanhMuc> getAllDanhMuc() throws RemoteException{
        return em.createNamedQuery("DanhMuc.findAll", DanhMuc.class).getResultList();
    }
    public void close() throws RemoteException{
        em.close();
    }
}
