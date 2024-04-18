package dao;

import bus.NhaXuatBan_Bus;
import entity.NhaXuatBan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class NhaXuatBan_Dao extends UnicastRemoteObject implements NhaXuatBan_Bus {

    private EntityManager em;
    public NhaXuatBan_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    @Override
    public int getThuTuNXB() throws RemoteException {
        Long count = em.createNamedQuery("NhaXuatBan.count", Long.class).getSingleResult();
        return count.intValue();
    }
    // Thêm nhà xuất bản vào CSDL

    @Override
    public boolean themNXB(NhaXuatBan nxb) throws RemoteException {
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
    @Override
    public boolean updateNXB(NhaXuatBan nxb) throws RemoteException{
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

    @Override
    public List<NhaXuatBan> timKiemNXB(String queryParams) throws RemoteException{
        return  em.createNamedQuery("NhaXuatBan.find", NhaXuatBan.class)
                .setParameter("maNXB", "%" + queryParams + "%")
                .setParameter("tenNXB", "%" + queryParams + "%")
                .setParameter("sdt", "%" + queryParams + "%")
                .getResultList();
    }

    @Override
    public List<NhaXuatBan> getAllNXB() throws RemoteException{
        return em.createNamedQuery("NhaXuatBan.findAll", NhaXuatBan.class).getResultList();
    }

}
