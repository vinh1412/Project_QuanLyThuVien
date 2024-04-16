package dao;

import bus.SanPham_Bus;
import entity.DanhMuc;
import entity.NhaCungCap;
import entity.SanPham;
import entity.TacGia;
import entity.TheLoai;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class SanPham_Dao extends UnicastRemoteObject implements SanPham_Bus {
    private EntityManager em;

    public SanPham_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }
    @Override
    public List<SanPham> getAllSanPham() throws RemoteException {
        return em.createNamedQuery("SanPham.findAll").getResultList();
    }

    @Override
    public boolean themSanPham(SanPham s) throws RemoteException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(s);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateSanPham(SanPham s) throws RemoteException{
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(s);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

//    public String getSanPhamByMaSP(String maSP) {
//        return em.createNamedQuery("SanPham.findSPByMaSanPham", SanPham.class).setParameter("maSanPham", maSP).getSingleResult().getTenSanPham();
//    }

    @Override
    public List<SanPham> timKiemSanPham(String queryParams) throws RemoteException{
        return em.createNamedQuery("SanPham.find", SanPham.class)
                .setParameter("tenSanPham", "%" + queryParams + "%")
                .setParameter("maSanPham", "%" + queryParams + "%")
                .getResultList();
    }
    @Override
    public int getThuTuSanPham() throws RemoteException{
        return em.createNamedQuery("SanPham.count", Long.class).getSingleResult().intValue()+1;
    }
    @Override
    public List<SanPham> locSanPham(NhaCungCap nhaCungCap, TacGia tacGia, DanhMuc danhMuc, TheLoai theLoai) throws RemoteException{
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

    @Override
    public SanPham getSanPhamTheoMa(String maSPham) throws RemoteException{
        return em.createNamedQuery("SanPham.getSPByMaSP", SanPham.class).setParameter("maSanPham", maSPham).getSingleResult();
    }
}
