package dao;


import entity.ChiTietHoaDon;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChiTietHoaDon_Dao {
    private EntityManager em;

    public ChiTietHoaDon_Dao() {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    public boolean themCTHD(ChiTietHoaDon chiTietHoaDon) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(chiTietHoaDon);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public List<ChiTietHoaDon> getAllCTHD() {
        return em.createNamedQuery("ChiTietHoaDon.getAllCTHD", ChiTietHoaDon.class).getResultList();
    }

    public List<ChiTietHoaDon> getChiTietByMaHD(String maHoaDon) {
        return em.createNamedQuery("ChiTietHoaDon.getChiTietByMaHD", ChiTietHoaDon.class).setParameter("maHoaDon", maHoaDon).getResultList();
    }

    public double getTongTienHoaDon(String maHD){
        return em.createNamedQuery("ChiTietHoaDon.getTongTienHoaDon", Double.class).setParameter("maHoaDon", maHD).getSingleResult();
    }

    public List<Object[]> getTopSanPhamBanChay(int limit, Date date, Date fromDate, Date endDate){
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
//    public Map<String, Double> getTopSanPhamBanChay1(int limit, LocalDate date, LocalDate fromDate, LocalDate endDate){
//        Map<String, Double> map=new HashMap<>();
//        String jpql = "SELECT cthd.sanPham.maSanPham, SUM(cthd.giaBan * cthd.soLuong) FROM ChiTietHoaDon cthd JOIN cthd.hoaDon hd ";
//
//        if (date != null && fromDate == null) {
//            jpql += "WHERE hd.ngayLap = :date ";
//        }
//        if (fromDate != null) {
//            jpql += "WHERE hd.ngayLap BETWEEN :fromDate AND :endDate ";
//        }
//
//        jpql += "GROUP BY cthd.sanPham.maSanPham " +
//                "ORDER BY SUM(cthd.giaBan * cthd.soLuong) DESC";
//        TypedQuery<Object[]> typedQuery = em.createQuery(jpql, Object[].class);
//        if (date != null) {
//            typedQuery.setParameter("date", java.sql.Date.valueOf(date));
//        }
//        if (fromDate != null && endDate != null) {
//            typedQuery.setParameter("fromDate", java.sql.Date.valueOf(fromDate));
//            typedQuery.setParameter("endDate", java.sql.Date.valueOf(endDate));
//        };
//        typedQuery.setMaxResults(limit);
//        for (Object[] o : typedQuery.getResultList()) {
//            map.put((String) o[0], (Double) o[1]);
//        }
//        return map;
//    }
//    public Map<String, Double> getTopSanPhamBanChay2(Date localDate){
//        String jpql = "SELECT cthd.sanPham.maSanPham, SUM(cthd.giaBan * cthd.soLuong) FROM ChiTietHoaDon cthd JOIN cthd.hoaDon hd " +
//                "WHERE hd.ngayLap = :ngayLap " +
//                "GROUP BY cthd.sanPham.maSanPham " +
//                "ORDER BY SUM(cthd.giaBan * cthd.soLuong) DESC";
//        TypedQuery<Object[]> typedQuery = em.createQuery(jpql, Object[].class);
//        int year = localDate.getYear() - 1900;
//        int month = localDate.getMonth() - 1;
//        int day = localDate.getDay();
//        localDate = new Date(localDate.getYear()-1900, localDate.getMonth()-1, localDate.getDate());
//        LocalDate date = localDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        date.format(formatter);
//        System.out.println(date.toString());
//        typedQuery.setParameter("ngayLap", java.sql.Date.valueOf(date));
//        typedQuery.setMaxResults(3);
//        Map<String, Double> map = new HashMap<>();
//        for (Object[] o : typedQuery.getResultList()) {
//            map.put((String) o[0], (Double) o[1]);
//        }
//        return map;
//    }
    public static void main(String[] args) {
        testGetTopSanPhamBanChay();
    }

    public static void testGetTopSanPhamBanChay() {
        // Khởi tạo HoaDonDao và các tham số cần thiết
        ChiTietHoaDon_Dao hoaDonDao = new ChiTietHoaDon_Dao(); // Khởi tạo đối tượng HoaDonDao
        int limit = 3; // Giới hạn số lượng sản phẩm được lấy
        Date date =  null;// Ngày để lấy các hóa đơn
        Date fromDate = new Date(2022, 7, 1);;// Ngày bắt đầu khoảng thời gian
        Date endDate = new Date(2022, 7, 31);; // Ngày kết thúc khoảng thời gian
        // Gọi phương thức getTopSanPhamBanChay
       List<Object[]> map = hoaDonDao.getTopSanPhamBanChay(limit, date, fromDate, endDate);
            // In ra màn hình
            for (Object[] o : map) {
                System.out.println("Mã sản phẩm: " + o[0] + ", Doanh thu: " + o[1]);
            }
    }
}
