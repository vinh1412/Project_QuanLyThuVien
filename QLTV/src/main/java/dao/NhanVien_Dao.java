package dao;

import bus.NhanVien_Bus;
import entity.NhanVien;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NhanVien_Dao extends UnicastRemoteObject implements NhanVien_Bus {
    private EntityManager em;

    public NhanVien_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    //Them Nhan Vien
    @Override
    public boolean themNhanVien(NhanVien nhanVien) throws RemoteException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(nhanVien);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int checkSDT(String sdt) throws RemoteException{
        Long count = em.createNamedQuery("NhanVien.checkSDT", Long.class).setParameter("soDienThoai", sdt).getSingleResult();
        return count.intValue();
    }

    // Lấy số lượng nhan vien
    @Override
    public int getSoLuongNV() throws RemoteException{
        Long count = em.createNamedQuery("NhanVien.countNV", Long.class).getSingleResult();
        return count.intValue();
    }

    //Lay Tat Ca Nhan Vien
    @Override
    public List<NhanVien> getAllNhanVien() throws RemoteException{
        return em.createNamedQuery("NhanVien.findAll", NhanVien.class).getResultList();
    }

    @Override
    public boolean capNhatNV(NhanVien nv) throws RemoteException{
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(nv);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    //Tim Kiem
    @Override
    public List<NhanVien> timNhanVien(String thongTin) throws RemoteException{
        return em.createNamedQuery("NhanVien.findNV", NhanVien.class)
                .setParameter("maNhanVien", "%" + thongTin + "%")
                .setParameter("tenNhanVien", "%" + thongTin + "%")
                .setParameter("soDienThoai", "%" + thongTin + "%")
                .setParameter("luongCoBan", Double.parseDouble(thongTin))
                .getResultList();
    }

    //Loc
    @Override
    public List<NhanVien> LayDanhSachNhanVienTheoTieuChi(int cv, int gt, int tt) throws RemoteException{
        List<NhanVien> dsNV = new ArrayList<>();
        String query = "SELECT nv FROM NhanVien nv WHERE 1=1";
        if (cv >= 0 && cv != 2) {
            query += " AND nv.chucVu = :chucVu";
        }
        if (gt >= 0 && gt != 3) {
            query += " AND nv.gioiTinh = :gioiTinh";
        }
        if (tt >= 0 && tt != 2) {
            query += " AND nv.trangThai = :trangThai";
        }

        TypedQuery<NhanVien> jpql = em.createQuery(query, NhanVien.class);
        if (cv >= 0 && cv != 2) {
            jpql.setParameter("chucVu", cv);
        }
        if (gt >= 0 && gt != 3) {
            jpql.setParameter("gioiTinh", gt);
        }
        if (tt >= 0 && tt != 2) {
            jpql.setParameter("trangThai", tt);
        }

        dsNV = jpql.getResultList();
        return dsNV;
    }

    //tim nhan vien theo ma
    @Override
    public NhanVien timNhanVienByMa(String maNV) throws RemoteException{
        return em.createNamedQuery("NhanVien.findNVByMaNV", NhanVien.class).setParameter("maNhanVien", maNV).getSingleResult();
    }
}
