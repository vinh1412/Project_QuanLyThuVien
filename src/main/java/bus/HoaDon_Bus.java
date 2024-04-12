package bus;

import dao.HoaDon_Dao;
import entity.HoaDon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDon_Bus {
    private final HoaDon_Dao hd_Dao;
    public HoaDon_Bus() {
        hd_Dao = new HoaDon_Dao();
    }
    public boolean themHD(HoaDon hd) {
        return hd_Dao.themHoaDon(hd);
    }
    public List<HoaDon> getAllHD() {
        return hd_Dao.getAllHD();
    }
    public int getThuTuHoaDon() throws SQLException {
        return hd_Dao.getThuTuHoaDon();
    }
    public List<HoaDon> getHoaDonByDateRange(Date fromDate, Date toDate, String maNV) {
        return hd_Dao.getHoaDonByDateRange(fromDate, toDate, maNV);
    }
    public List<HoaDon> getHoaDonByDate(Date date, String maNV) {
        return hd_Dao.getHoaDonByDate(date, maNV);
    }
    public List<HoaDon> getHoaDonByMonthYear(int month, int year, String maNV) {
        return hd_Dao.getHoaDonByMonthYear(month, year, maNV);
    }
    public List<HoaDon> getHoaDonByNhanVien(String maNV) {
        return hd_Dao.getAllHDByNhanVien(maNV);
    }
    public List<HoaDon> timHoaDon(String thongTin, String maNV) {
        return hd_Dao.timKiemHD(thongTin, maNV);
    }
}
