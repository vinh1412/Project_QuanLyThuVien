package dao;


import bus.ChiTietHoaDon_Bus;
import entity.ChiTietHoaDon;

import jakarta.persistence.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChiTietHoaDon_Dao extends UnicastRemoteObject implements ChiTietHoaDon_Bus {
        private EntityManager em;

    public ChiTietHoaDon_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    @Override
    public boolean themCTHD(ChiTietHoaDon chiTietHoaDon) throws RemoteException{
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(chiTietHoaDon); //
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ChiTietHoaDon> getAllCTHD() throws RemoteException{
        return em.createNamedQuery("ChiTietHoaDon.getAllCTHD", ChiTietHoaDon.class).getResultList();
    }
    @Override
    public List<ChiTietHoaDon> getChiTietByMa(String maHoaDon) throws RemoteException{
        return em.createNamedQuery("ChiTietHoaDon.getChiTietByMaHD", ChiTietHoaDon.class).setParameter("maHoaDon", maHoaDon).getResultList();
    }
    @Override
    public double getTongTienHoaDon(String maHD)throws RemoteException{
        return em.createNamedQuery("ChiTietHoaDon.getTongTienHoaDon", Double.class).setParameter("maHoaDon", maHD).getSingleResult();
    }
    @Override
    public List<Object[]> getSpBanChay(int limit, Date date, Date fromDate, Date endDate)throws RemoteException{
        String jpql = "SELECT cthd.sanPham.maSanPham, SUM(cthd.giaBan * cthd.soLuong) FROM ChiTietHoaDon cthd JOIN cthd.hoaDon hd ";

        if (date != null && fromDate == null) {
            jpql += "WHERE hd.ngayLap = :date ";
        }
        if (fromDate != null) {
            jpql += "WHERE hd.ngayLap BETWEEN :fromDate AND :endDate ";
        }

        jpql += "GROUP BY cthd.sanPham.maSanPham " +
                "ORDER BY SUM(cthd.giaBan * cthd.soLuong) DESC";
        TypedQuery<Object[]> typedQuery = em.createQuery(jpql, Object[].class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (date != null) {
            date=new Date(date.getYear()-1900, date.getMonth()-1, date.getDate());
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            localDate.format(formatter);
            typedQuery.setParameter("date", java.sql.Date.valueOf(localDate));
        }
        if (fromDate != null && endDate != null) {
            fromDate=new Date(fromDate.getYear()-1900, fromDate.getMonth()-1, fromDate.getDate());
            endDate=new Date(endDate.getYear()-1900, endDate.getMonth()-1, endDate.getDate());
            LocalDate localFromDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            localFromDate.format(formatter);
            localEndDate.format(formatter);
            typedQuery.setParameter("fromDate", fromDate, TemporalType.DATE);
            typedQuery.setParameter("endDate", endDate, TemporalType.DATE);
        };
        typedQuery.setMaxResults(limit);
        return typedQuery.getResultList();
    }

    @Override
    public List<Object[]> getChiTietHoaDonByMaHD(String maHoaDon) throws RemoteException {
            String jpql = "SELECT cthd.soLuong, cthd.giaBan, sp.tenSanPham, nv.tenNhanVien, hd.maHoaDon, " +
                    "hd.giamGia, CASE WHEN hd.khachHang.maKhachHang IS NOT NULL THEN kh.tenKhachHang END, " +
                    "hd.ngayLap, (cthd.soLuong * cthd.giaBan) as tongTien, " +
                    "(SUM((cthd.soLuong * cthd.giaBan)) OVER (PARTITION BY cthd.hoaDon.maHoaDon) - hd.giamGia) as tongTienHoaDon " +
                    "FROM ChiTietHoaDon cthd " +
                    "INNER JOIN HoaDon hd ON cthd.hoaDon.maHoaDon = hd.maHoaDon " +
                    "INNER JOIN NhanVien nv ON hd.nhanVien.maNhanVien = nv.maNhanVien " +
                    "INNER JOIN SanPham sp ON cthd.sanPham.maSanPham = sp.maSanPham " +
                    "LEFT JOIN KhachHang kh ON hd.khachHang.maKhachHang = kh.maKhachHang " +
                    "WHERE cthd.hoaDon.maHoaDon = :maHoaDon";

            Query query = em.createQuery(jpql, Object[].class);
            query.setParameter("maHoaDon", maHoaDon);

            return query.getResultList();
    }

}
