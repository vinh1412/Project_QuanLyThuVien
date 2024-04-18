package bus;

import entity.NhaCungCap;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NhaCungCap_Bus extends Remote {
    public int getThuTuNCC() throws RemoteException;

    public boolean themNCC(NhaCungCap ncc) throws RemoteException;

    public boolean updateNCC(NhaCungCap ncc) throws RemoteException;

    public List<NhaCungCap> getAllNCC() throws RemoteException;

    public List<NhaCungCap> timKiemNCC(String queryParams) throws RemoteException;
}
