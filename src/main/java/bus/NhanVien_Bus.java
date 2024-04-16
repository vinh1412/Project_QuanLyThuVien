/*
 * @ {#} NhanVien_Bus.java   1.0     17/04/2024
 *
 * Copyright (c) 2024 IUH. All rights reserved.
 */

package bus;

import entity.NhanVien;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/*
 * @description:
 * @author: Tran Hien Vinh
 * @date:   17/04/2024
 * @version:    1.0
 */
public interface NhanVien_Bus extends Remote {
    public boolean themNhanVien(NhanVien nhanVien) throws RemoteException;
    public int checkSDT(String sdt) throws RemoteException;
    public int getSoLuongNV() throws RemoteException;
    public List<NhanVien> getAllNhanVien() throws RemoteException;
    public boolean capNhatNV(NhanVien nv) throws RemoteException;
    public List<NhanVien> timNhanVien(String thongTin) throws RemoteException;
    public List<NhanVien> LayDanhSachNhanVienTheoTieuChi(int cv, int gt, int tt) throws RemoteException;
    public NhanVien timNhanVienByMa(String maNV) throws RemoteException;
}
