package dao;

import bus.NhaCungCap_Bus;
import entity.NhaCungCap;
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

public class NhaCungCap_Dao extends UnicastRemoteObject implements NhaCungCap_Bus {
    private EntityManager em;

    public NhaCungCap_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    @Override
    public int getThuTuNCC() throws RemoteException{
        Long count = em.createNamedQuery("NhaCungCap.countNCC", Long.class).getSingleResult();
        return count.intValue();
    }
    // Thêm nhà cung cấp vào CSDL
    @Override
    public boolean themNCC(NhaCungCap ncc) throws RemoteException {
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
    @Override
    public boolean updateNCC(NhaCungCap ncc) throws RemoteException{
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
    @Override
    public List<NhaCungCap> timKiemNCC(String queryParams) throws RemoteException{
        return em.createNamedQuery("NhaCungCap.find")
                .setParameter("maNhaCungCap", "%" + queryParams + "%")
                .setParameter("tenNhaCungCap", "%" + queryParams + "%")
                .setParameter("diaChi", "%" + queryParams + "%")
                .setParameter("soDienThoai", "%" + queryParams + "%")
                .getResultList();
    }

    @Override
    public List<NhaCungCap> getAllNCC() throws RemoteException{
        return em.createNamedQuery("NhaCungCap.findAll", NhaCungCap.class).getResultList();
    }
}
