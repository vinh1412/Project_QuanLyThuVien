package dao;

import bus.HoaDon_Bus;
import entity.HoaDon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;

public class HoaDon_Dao extends UnicastRemoteObject implements HoaDon_Bus {
    private EntityManager em;

    public HoaDon_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    @Override
    public int getThuTuHoaDon() throws RemoteException{
        Long count = em.createNamedQuery("HoaDon.getThuTuHoaDon", Long.class).getSingleResult();
        return count.intValue();
    }
    @Override
    public boolean themHD(HoaDon hoaDon) throws RemoteException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(hoaDon);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public List<HoaDon> getAllHD() throws RemoteException{
        return em.createNamedQuery("HoaDon.getAllHoaDon", HoaDon.class).getResultList();
    }
    @Override
    public List<HoaDon> getHoaDonByNhanVien(String maNV) throws RemoteException{
        return em.createNamedQuery("HoaDon.getAllHDByNhanVien", HoaDon.class)
                .setParameter("maNhanVien", maNV)
                .getResultList();
    }
    @Override
    public List<HoaDon> getHoaDonByDateRange(Date fromDate, Date toDate, String maNV) throws RemoteException{
        String jpql = "SELECT hd FROM HoaDon hd WHERE hd.ngayLap BETWEEN :fromDate AND :toDate";
        if (!maNV.isEmpty()) {
            jpql += " AND hd.nhanVien.maNhanVien = :maNV";
        }
        TypedQuery<HoaDon> query = em.createQuery(jpql, HoaDon.class);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        if (!maNV.isEmpty()) {
            query.setParameter("maNV", maNV);
        }
        return query.getResultList();
    }
    @Override
    public List<HoaDon> getHoaDonByDate(Date date, String maNV) throws RemoteException{
        String jpql = "SELECT hd FROM HoaDon hd WHERE hd.ngayLap = :date";
        if (!maNV.isEmpty()) {
            jpql += " AND hd.nhanVien.maNhanVien = :maNV";
        }
        TypedQuery<HoaDon> query = em.createQuery(jpql, HoaDon.class);
        query.setParameter("date", date);
        if (!maNV.isEmpty()) {
            query.setParameter("maNV", maNV);
        }
        return query.getResultList();
    }
    @Override
    public List<HoaDon> getHoaDonByMonthYear(int month, int year, String maNV) throws RemoteException{
        String jpql = "SELECT hd FROM HoaDon hd WHERE FUNCTION('YEAR', hd.ngayLap) = :year AND FUNCTION('MONTH', hd.ngayLap) = :month";
        if (!maNV.isEmpty()) {
            jpql += " AND hd.nhanVien.maNhanVien = :maNV";
        }
        TypedQuery<HoaDon> query = em.createQuery(jpql, HoaDon.class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        if (!maNV.isEmpty()) {
            query.setParameter("maNV", maNV);
        }
        return query.getResultList();
    }
    @Override
    public List<HoaDon> timHoaDon(String query, String maNV) throws RemoteException{
        return em.createNamedQuery("HoaDon.findHoaDon", HoaDon.class)
                .setParameter("query", "%" + query + "%")
                .setParameter("maNV", "%" + maNV + "%")
                .getResultList();
    }

}
