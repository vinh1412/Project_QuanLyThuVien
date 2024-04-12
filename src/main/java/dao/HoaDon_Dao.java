package dao;

import entity.HoaDon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;

public class HoaDon_Dao {
    private EntityManager em;

    public HoaDon_Dao() {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    public int getThuTuHoaDon(){
        return em.createNamedQuery("HoaDon.getThuTuHoaDon", Integer.class).getSingleResult();
    }

    public boolean themHoaDon(HoaDon hoaDon) {
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

    public List<HoaDon> getAllHD() {
        return em.createNamedQuery("HoaDon.getAllHoaDon", HoaDon.class).getResultList();
    }
    public List<HoaDon> getAllHDByNhanVien(String maNV) {
        return em.createNamedQuery("HoaDon.getAllHDByNhanVien", HoaDon.class)
                .setParameter("maNhanVien", maNV)
                .getResultList();
    }

    public List<HoaDon> getHoaDonByDateRange(Date fromDate, Date toDate, String maNV) {
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

    public List<HoaDon> getHoaDonByDate(Date date, String maNV) {
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
    public List<HoaDon> getHoaDonByMonthYear(int month, int year, String maNV) {
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

    public List<HoaDon> timKiemHD(String query, String maNV) {
        return em.createNamedQuery("HoaDon.findHoaDon", HoaDon.class)
                .setParameter("query", "%" + query + "%")
                .setParameter("maNV", "%" + maNV + "%")
                .getResultList();
    }

    public static void main(String[] args) {
        HoaDon_Dao hoaDonDao=new HoaDon_Dao();
        List<HoaDon> kq=hoaDonDao.timKiemHD("HD-13122306","NV-0804");
        kq.forEach(hoaDon -> System.out.println(hoaDon));
    }
}
