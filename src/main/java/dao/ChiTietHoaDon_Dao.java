package dao;


import entity.ChiTietHoaDon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

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
        if (date != null) {
            typedQuery.setParameter("date", date);
        }
        if (fromDate != null && endDate != null) {
            typedQuery.setParameter("fromDate", fromDate);
            typedQuery.setParameter("endDate", endDate);
        };
        typedQuery.setMaxResults(limit);
        return typedQuery.getResultList();
    }
    public Map<String, Double> getTopSanPhamBanChay1(int limit, Date date, Date fromDate, Date endDate){
        Map<String, Double> map=new HashMap<>();

        List<?> re=em.createNamedQuery("ChiTietHoaDon.getTopSanPhamBanChay").setParameter("fromDate", fromDate).setParameter("endDate", endDate).getResultList();
        for (Object obj : re) {
            Object[] o = (Object[]) obj;
            map.put((String) o[0], (Double) o[1]);
        }
        return map;
    }

    public static void main(String[] args) {
        testGetTopSanPhamBanChay();
    }

    public static void testGetTopSanPhamBanChay() {
        // Khởi tạo HoaDonDao và các tham số cần thiết
        ChiTietHoaDon_Dao hoaDonDao = new ChiTietHoaDon_Dao(); // Khởi tạo đối tượng HoaDonDao
        int limit = 2; // Giới hạn số lượng sản phẩm được lấy
        Date date = null; // Ngày để lấy các hóa đơn
        Date fromDate = new Date(2022, 7, 1); // Ngày bắt đầu khoảng thời gian
        Date endDate = new Date(2022, 7, 30); // Ngày kết thúc khoảng thời gian


            // Gọi phương thức getTopSanPhamBanChay và lấy kết quả trả về
            Map<String,Double> result = hoaDonDao.getTopSanPhamBanChay1(limit, date, fromDate, endDate);
            // Hiển thị kết quả
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println("Mã sản phẩm: " + entry.getKey() + " - Tổng tiền: " + entry.getValue());
            }
    }
}
