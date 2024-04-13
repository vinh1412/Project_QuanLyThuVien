package dao;

import entity.DanhMuc;
import entity.NhaCungCap;
import entity.SanPham;
import entity.TacGia;
import entity.TheLoai;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SanPham_Dao {
    private EntityManager em;

    public SanPham_Dao() {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    public List<SanPham> getAllSanPham() {
        return em.createNamedQuery("SanPham.findAll").getResultList();
    }

    public boolean themSanPham(SanPham sp) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(sp);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSanPham(SanPham sp){
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(sp);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public String getSanPhamByMaSP(String maSP) {
        return em.createNamedQuery("SanPham.findSPByMaSanPham", SanPham.class).setParameter("maSanPham", maSP).getSingleResult().getTenSanPham();
    }

    public List<SanPham> timKiemSanPham(String searchTerm) {
        return em.createNamedQuery("SanPham.find", SanPham.class)
                .setParameter("tenSanPham", "%" + searchTerm + "%")
                .setParameter("maSanPham", "%" + searchTerm + "%")
                .getResultList();
    }

    public int getThuTuSP(){
        return em.createNamedQuery("SanPham.count", Long.class).getSingleResult().intValue()+1;
    }
    public List<SanPham> locSanPham(NhaCungCap nhaCungCap, TacGia tacGia, DanhMuc danhMuc, TheLoai theLoai) {
        String jpql="SELECT sp FROM SanPham sp JOIN NhaCungCap n ON sp.nhaCungCap.maNhaCungCap = n.maNhaCungCap JOIN TheLoai tl ON sp.theLoai.maTheLoai = tl.maTheLoai JOIN DanhMuc d ON tl.danhMuc.maDanhMuc = d.maDanhMuc WHERE 1 = 1";
        if (nhaCungCap != null && nhaCungCap.getMaNhaCungCap() != null && !nhaCungCap.getMaNhaCungCap().isEmpty()) {
            jpql += " AND sp.nhaCungCap.maNhaCungCap = :maNhaCungCap";
        }
        if (tacGia != null && tacGia.getMaTacGia() != null && !tacGia.getMaTacGia().isEmpty()) {
            jpql += " AND sp.tacGia.maTacGia = :maTacGia";
        }
        if (danhMuc != null && danhMuc.getMaDanhMuc() != 0) {
            jpql += " AND tl.danhMuc.maDanhMuc = :maDanhMuc";
        }
        if (theLoai != null && theLoai.getMaTheLoai() != 0) {
            jpql += " AND sp.theLoai.maTheLoai = :maTheLoai";
        }
        TypedQuery<SanPham> query = em.createQuery(jpql, SanPham.class);
        if (nhaCungCap != null && nhaCungCap.getMaNhaCungCap() != null && !nhaCungCap.getMaNhaCungCap().isEmpty()) {
            query.setParameter("maNhaCungCap", nhaCungCap.getMaNhaCungCap());
        }
        if (tacGia != null && tacGia.getMaTacGia() != null && !tacGia.getMaTacGia().isEmpty()) {
            query.setParameter("maTacGia", tacGia.getMaTacGia());
        }
        if (danhMuc != null && danhMuc.getMaDanhMuc() != 0) {
            query.setParameter("maDanhMuc", danhMuc.getMaDanhMuc());
        }
        if (theLoai != null && theLoai.getMaTheLoai() != 0) {
            query.setParameter("maTheLoai", theLoai.getMaTheLoai());
        }
        return query.getResultList();
    }

    public SanPham getSanPhamTheoMa(String maSPham) {
        return em.createNamedQuery("SanPham.getSPByMaSP", SanPham.class).setParameter("maSanPham", maSPham).getSingleResult();
    }

    public static void main(String[] args) {
        SanPham_Dao spDao = new SanPham_Dao();
        SanPham sps = spDao.getSanPhamTheoMa("SP-01");
        System.out.println(sps);
    }
}
