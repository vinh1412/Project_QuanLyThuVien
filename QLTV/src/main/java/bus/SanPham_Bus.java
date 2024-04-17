package bus;

import dao.SanPham_Dao;
import entity.DanhMuc;
import entity.NhaCungCap;
import entity.SanPham;
import entity.TacGia;
import entity.TheLoai;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface SanPham_Bus  extends Remote {
    
    public int getThuTuSanPham() throws RemoteException;
    
    public boolean themSanPham(SanPham s) throws RemoteException;
    
    public boolean updateSanPham(SanPham s) throws RemoteException;
    
    public List<SanPham> getAllSanPham() throws RemoteException;
    
    public List<SanPham> timKiemSanPham(String queryParams) throws RemoteException;
    
    public  List<SanPham> locSanPham(NhaCungCap nhaCungCap, TacGia tacGia, DanhMuc danhMuc, TheLoai theLoai) throws RemoteException;
    
//    public String getTenSPByMa(String maSP) throws RemoteException;
    
    public SanPham getSanPhamTheoMa(String maSPham) throws RemoteException;
}
