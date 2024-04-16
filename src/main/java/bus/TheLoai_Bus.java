package bus;

import dao.TheLoai_Dao;
import entity.TheLoai;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface TheLoai_Bus extends Remote {

    public boolean themTheLoai(String tenTheLoai, int maDanhMuc) throws RemoteException;
    
    public boolean updateTheLoai(TheLoai theLoai) throws RemoteException;
    
    public List<TheLoai> getAllTheLoai() throws RemoteException;
    
    public List<TheLoai> timKiemTheLoai(String queryParams) throws RemoteException;
}
