/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import bus.TacGia_Bus;
import entity.TacGia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
public class TacGia_Dao extends UnicastRemoteObject implements TacGia_Bus {

    private EntityManager em;

    public TacGia_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    @Override
    public int getThuTuTacGia() throws RemoteException{
        return em.createNamedQuery("TacGia.count", Long.class).getSingleResult().intValue();
    }

    @Override
    public boolean themTacGia(TacGia tg) throws RemoteException{
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
    @Override
    public boolean updateTacGia(TacGia tg) throws RemoteException{
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
    @Override
    public List<TacGia> getAllTacGia() throws RemoteException{
        return em.createNamedQuery("TacGia.findAll", TacGia.class).getResultList();
    }

    @Override
    public List<TacGia> timKiemTacGia(String queryParams) throws RemoteException{
        return em.createNamedQuery("TacGia.find", TacGia.class)
                .setParameter("maTG", "%" + queryParams + "%")
                .setParameter("tenTG", "%" + queryParams + "%")
                .setParameter("sdt", "%" + queryParams + "%")
                .getResultList();
    }
}
