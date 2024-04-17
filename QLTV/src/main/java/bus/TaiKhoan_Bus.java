/*
 * @ {#} TaiKhoan_Bus.java   1.0     17/04/2024
 *
 * Copyright (c) 2024 IUH. All rights reserved.
 */

package bus;

import entity.NhanVien;
import entity.TaiKhoan;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * @description:
 * @author: Tran Hien Vinh
 * @date:   17/04/2024
 * @version:    1.0
 */
public interface TaiKhoan_Bus extends Remote {
    public TaiKhoan getTaiKhoanByTen(String tenDn) throws RemoteException;
    public boolean themTaiKhoan(NhanVien nv) throws RemoteException;
    public boolean updateMatKhau(String tenTaiKhoan, String matKhau) throws RemoteException;
}
