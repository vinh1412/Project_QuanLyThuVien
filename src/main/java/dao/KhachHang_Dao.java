/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import bus.KhachHang_Bus;
import entity.KhachHang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class KhachHang_Dao extends UnicastRemoteObject implements KhachHang_Bus {

    private EntityManager em;

    public KhachHang_Dao() throws RemoteException {
        em = Persistence.createEntityManagerFactory("JPA_MSSQL").createEntityManager();
    }

    //them khach hang
    @Override
    public boolean themKhachHang(KhachHang khachHang) throws RemoteException{
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(khachHang);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int checkSDT(String sdt) throws RemoteException {
        Long count = em.createNamedQuery("KhachHang.checkSDT", Long.class).setParameter("soDienThoai", sdt).getSingleResult();
        return count.intValue();
    }

    @Override
    public int getSoLuongKH() throws RemoteException{
        Long count = em.createNamedQuery("KhachHang.count", Long.class).getSingleResult();
        return count.intValue();
    }

    @Override
    public List<KhachHang> getAllKhachHang() throws RemoteException{
        return em.createNamedQuery("KhachHang.findAll", KhachHang.class).getResultList();
    }

    @Override
    public boolean capNhatKH(KhachHang kh) throws RemoteException{
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(kh);
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<KhachHang> timKhachHang(String thongTin) throws RemoteException{
        String thongTinLike = "%" + thongTin.toLowerCase() + "%";
        int gt = -1;
        if (thongTin.equalsIgnoreCase("Nam")) {
            gt = 0;
        } else if (thongTin.equalsIgnoreCase("Nữ")) {
            gt = 1;
        } else if (thongTin.equalsIgnoreCase("Khác") || thongTin.equalsIgnoreCase("Không xác định")) {
            gt = 2;
        }
        TypedQuery<KhachHang> query = em.createNamedQuery("KhachHang.find", KhachHang.class)
                .setParameter("maKH", "%" + thongTinLike+ "%")
                .setParameter("tenKH", "%" + thongTinLike + "%")
                .setParameter("sdt", "%" + thongTinLike + "%");
        if (gt >= 0) { // Chỉ thêm tham số này nếu giới tính được xác định
            query.setParameter("gioiTinh", gt);
        }
        return query.getResultList();
    }

    @Override
    public KhachHang timKiemKhachHangTheoSDT(String sdt) throws RemoteException{
        return em.createNamedQuery("KhachHang.findKHBySDT", KhachHang.class).setParameter("sdt", sdt).getSingleResult();
    }


    //TIM THEO TEN
    @Override
    public KhachHang getKhachHangByTen(String tenKhachHang) throws RemoteException{
        return em.createNamedQuery("KhachHang.findKHByTenKH", KhachHang.class).setParameter("tenKH", tenKhachHang).getSingleResult();
    }
}
