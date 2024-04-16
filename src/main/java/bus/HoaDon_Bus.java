package bus;

import dao.HoaDon_Dao;
import entity.HoaDon;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface HoaDon_Bus extends Remote {

    public boolean themHD(HoaDon hd) throws RemoteException;
    public List<HoaDon> getAllHD() throws RemoteException;
    public int getThuTuHoaDon() throws RemoteException;
    public List<HoaDon> getHoaDonByDateRange(Date fromDate, Date toDate, String maNV) throws RemoteException;
    public List<HoaDon> getHoaDonByDate(Date date, String maNV) throws RemoteException;
    public List<HoaDon> getHoaDonByMonthYear(int month, int year, String maNV) throws RemoteException;
    public List<HoaDon> getHoaDonByNhanVien(String maNV) throws RemoteException;
    public List<HoaDon> timHoaDon(String thongTin, String maNV) throws RemoteException;
}
