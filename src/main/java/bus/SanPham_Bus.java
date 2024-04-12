package bus;

import dao.SanPham_Dao;
import entity.DanhMuc;
import entity.NhaCungCap;
import entity.SanPham;
import entity.TacGia;
import entity.TheLoai;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPham_Bus {
    private final SanPham_Dao sp_Dao;
    public SanPham_Bus() {
        sp_Dao = new SanPham_Dao();
    }
    
    public int getThuTuSanPham() throws SQLException {
        return sp_Dao.getThuTuSP();
    }
    
    public boolean themSanPham(SanPham s) {
        return sp_Dao.themSanPham(s);
    }
    
    public boolean updateSanPham(SanPham s) {
        return sp_Dao.updateSanPham(s);
    }
    
    public List<SanPham> getAllSanPham() {
        return sp_Dao.getAllSanPham();
    }
    
    public List<SanPham> timKiemSanPham(String queryParams) {
        return sp_Dao.timKiemSanPham(queryParams);
    }
    
    public  List<SanPham> locSanPham(NhaCungCap nhaCungCap, TacGia tacGia, DanhMuc danhMuc, TheLoai theLoai) {
        return sp_Dao.locSanPham(nhaCungCap, tacGia, danhMuc, theLoai);
    }
    
    public String getTenSPByMa(String maSP) {
        return sp_Dao.getSanPhamByMaSP(maSP);
    }
    
    public SanPham getSanPhamTheoMa(String maSPham) {
        return sp_Dao.getSanPhamTheoMa(maSPham);
    }
}
