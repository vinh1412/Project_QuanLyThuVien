/*
 * @ {#} Server.java   1.0     18/04/2024
 *
 * Copyright (c) 2024 IUH. All rights reserved.
 */

package server;

import bus.*;
import dao.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/*
 * @description:
 * @author: Nguyen Tan Thai Duong
 * @date:   18/04/2024
 * @version:    1.0
 */
public class Server {
        private static final String URL = "rmi://172.21.89.101:5151/";
        public static void main(String[] args) throws NamingException, RemoteException {
            Context context = new InitialContext();
            ChiTietHoaDon_Bus chiTietHoaDon_Bus = new ChiTietHoaDon_Dao();
            DanhMuc_Bus danhMuc_Bus = new DanhMuc_Dao();
            HoaDon_Bus hoaDon_Bus = new HoaDon_Dao();
            KhachHang_Bus khachHang_Bus = new KhachHang_Dao();
            NhaCungCap_Bus nhaCungCap_Bus = new NhaCungCap_Dao();
            NhanVien_Bus nhanVien_Bus = new NhanVien_Dao();
            NhaXuatBan_Bus nhaXuatBan_Bus = new NhaXuatBan_Dao();
            SanPham_Bus sanPham_Bus = new SanPham_Dao();
            TacGia_Bus tacGia_Bus = new TacGia_Dao();
            TaiKhoan_Bus taiKhoan_Bus = new TaiKhoan_Dao();
            TheLoai_Bus theLoai_Bus = new TheLoai_Dao();

            LocateRegistry.createRegistry(5151);
            context.rebind(URL + "ChiTietHoaDon", chiTietHoaDon_Bus);
            context.rebind(URL + "DanhMuc", danhMuc_Bus);
            context.rebind(URL + "HoaDon", hoaDon_Bus);
            context.rebind(URL + "KhachHang", khachHang_Bus);
            context.rebind(URL + "NhaCungCap", nhaCungCap_Bus);
            context.rebind(URL + "NhanVien", nhanVien_Bus);
            context.rebind(URL + "NhaXuatBan", nhaXuatBan_Bus);
            context.rebind(URL + "SanPham", sanPham_Bus);
            context.rebind(URL + "TacGia", tacGia_Bus);
            context.rebind(URL + "TaiKhoan", taiKhoan_Bus);
            context.rebind(URL + "TheLoai", theLoai_Bus);

            System.out.println("Server is running...");
        }
}
