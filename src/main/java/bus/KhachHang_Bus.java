/*
 * @ {#} KhachHang_Bus.java   1.0     17/04/2024
 *
 * Copyright (c) 2024 IUH. All rights reserved.
 */

package bus;

import entity.KhachHang;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/*
 * @description:
 * @author: Tran Hien Vinh
 * @date:   17/04/2024
 * @version:    1.0
 */
public interface KhachHang_Bus extends Remote {
    public boolean themKhachHang(KhachHang khachHang) throws RemoteException;

    public int checkSDT(String sdt) throws RemoteException;

    public int getSoLuongKH() throws RemoteException;

    public List<KhachHang> getAllKhachHang() throws RemoteException;

    public boolean capNhatKH(KhachHang kh) throws RemoteException;

    public List<KhachHang> timKhachHang(String thongTin) throws RemoteException;

    public KhachHang timKiemKhachHangTheoSDT(String sdt) throws RemoteException;

    public KhachHang getKhachHangByTen(String tenKhachHang) throws RemoteException;
}
