package bus;

import dao.ChiTietHoaDon_Dao;
import entity.ChiTietHoaDon;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ChiTietHoaDon_Bus extends Remote {
    public boolean themCTHD(ChiTietHoaDon cthd) throws RemoteException;
    public List<ChiTietHoaDon> getAllCTHD() throws RemoteException;
    public List<ChiTietHoaDon> getChiTietByMa(String maHoaDon) throws RemoteException;
    public double getTongTienHoaDon(String maHD) throws RemoteException;
    public List<Object[]> getSpBanChay(int limit, Date date, Date fromDate, Date endDate) throws RemoteException;
    public List<Object[]> getChiTietHoaDonByMaHD(String maHoaDon) throws RemoteException;
}
