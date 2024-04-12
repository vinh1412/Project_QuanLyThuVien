package bus;

import dao.ChiTietHoaDon_Dao;
import entity.ChiTietHoaDon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChiTietHoaDon_Bus {
    private final ChiTietHoaDon_Dao cthd_Dao;
    public ChiTietHoaDon_Bus() {
        cthd_Dao = new ChiTietHoaDon_Dao();
    }
    public boolean themCTHD(ChiTietHoaDon cthd) {
        return cthd_Dao.themCTHD(cthd);
    }
    public List<ChiTietHoaDon> getAllCTHD() {
        return cthd_Dao.getAllCTHD();
    }
    public List<ChiTietHoaDon> getChiTietByMa(String maHoaDon) {
        return cthd_Dao.getChiTietByMaHD(maHoaDon);
    }
    public double getTongTienHoaDon(String maHD) throws SQLException {
        return cthd_Dao.getTongTienHoaDon(maHD);
    }
    public List<Object[]> getSpBanChay(int limit, Date date, Date fromDate, Date endDate) throws SQLException {
        return cthd_Dao.getTopSanPhamBanChay(limit, date, fromDate, endDate);
    }
}
