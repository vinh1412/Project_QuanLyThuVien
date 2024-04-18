package bus;

import entity.TacGia;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TacGia_Bus extends Remote {

    public int getThuTuTacGia() throws RemoteException;

    public boolean themTacGia(TacGia tg) throws RemoteException;

    public boolean updateTacGia(TacGia tg) throws RemoteException;

    public List<TacGia> getAllTacGia() throws RemoteException;

    public List<TacGia> timKiemTacGia(String queryParams) throws RemoteException;
}
