/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import bus.TaiKhoan_Bus;
import entity.TaiKhoan;
import entity.NhanVien;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TaiKhoan_Dao extends UnicastRemoteObject implements TaiKhoan_Bus {
    private EntityManager em;

    public TaiKhoan_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    @Override
    public TaiKhoan getTaiKhoanByTen(String tenDn) throws RemoteException {
        return (TaiKhoan) em.createNamedQuery("TaiKhoan.findByTenTaiKhoan").setParameter("tenTaiKhoan", tenDn).getSingleResult();
    }

    @Override
    public boolean themTaiKhoan(NhanVien nv) throws RemoteException{
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

    @Override
    public boolean updateMatKhau(String tenTaiKhoan, String matKhau) throws RemoteException{
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
}
