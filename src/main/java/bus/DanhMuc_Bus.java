package bus;

import dao.DanhMuc_Dao;
import entity.DanhMuc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface DanhMuc_Bus extends Remote {

    public boolean themDanhMuc(String tenDanhMuc) throws RemoteException;
    
    public boolean updateDanhMuc(DanhMuc danhMuc) throws RemoteException;
    
    public List<DanhMuc> getAllDanhMuc() throws RemoteException;
    
    public List<DanhMuc> timKiemDanhMuc(String queryParams) throws RemoteException;
}
