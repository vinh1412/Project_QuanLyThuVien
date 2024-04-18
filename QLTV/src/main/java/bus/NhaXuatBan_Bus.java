package bus;

import entity.NhaXuatBan;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NhaXuatBan_Bus extends Remote {

    public int getThuTuNXB() throws RemoteException;

    public boolean themNXB(NhaXuatBan nxb) throws RemoteException;

    public boolean updateNXB(NhaXuatBan nxb) throws RemoteException;

    public List<NhaXuatBan> getAllNXB() throws RemoteException;

    public List<NhaXuatBan> timKiemNXB(String queryParams) throws RemoteException;
}
