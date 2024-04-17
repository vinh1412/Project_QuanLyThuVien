package bus;

import dao.NhaCungCap_Dao;
import entity.NhaCungCap;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface NhaCungCap_Bus extends Remote {
    public int getThuTuNCC() throws RemoteException;
    
    public boolean themNCC(NhaCungCap ncc) throws RemoteException;
    
    public boolean updateNCC(NhaCungCap ncc) throws RemoteException;
    
    public List<NhaCungCap> getAllNCC() throws RemoteException;
    
    public List<NhaCungCap> timKiemNCC(String queryParams) throws RemoteException;
}
