package utils;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ngoct
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateID {

    public static String generateMaTacGia(int soThuTu) {
        // Lấy năm hiện tại
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy");
        String namHienTai = yearFormat.format(new Date());

        // Tạo mã tác giả
        String maTacGia = "TG-" + namHienTai + String.format("%02d", soThuTu);

        return maTacGia;
    }

    public static String generateMaNXB(int soThuTu) {
        // Lấy năm hiện tại
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy");
        String namHienTai = yearFormat.format(new Date());

        // Tạo mã tác giả
        String maNXB = "NXB-" + namHienTai + String.format("%02d", soThuTu);

        return maNXB;
    }
    
    
    public static String generateMaNCC(int soThuTu) {
        // Lấy năm hiện tại
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy");
        String namHienTai = yearFormat.format(new Date());

        // Tạo mã tác giả
        String maNXB = "NCC-" + namHienTai + String.format("%02d", soThuTu);

        return maNXB;
    }

    public static String generateMaSP(int soThuTu) {
        // Tạo mã tác giả
        String maSanPham = "SP-" + String.format("%02d", soThuTu);

        return maSanPham;
    }

    public static String generateMaNV(int soThuTu, Date ngayVaoLam) {
        String yearPart = String.format("%02d", ngayVaoLam);
        String employeeCounterPart = String.format("%02d", soThuTu + 1);
        String employeeID = "NV-" + yearPart + employeeCounterPart;
        return employeeID;
    }

    public static String generateMaKH(int soThuTu, Date namSinh) {
        String yearPart = String.format("%02d", namSinh);
        String employeeCounterPart = String.format("%02d", soThuTu + 1);
        String maKH = "KH-" + yearPart + employeeCounterPart;
        return maKH;
    }
    
    public static String generateMaHoaDon(int soThuTu) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
        String ngayThangNam = dateFormat.format(currentDate);

        String maHoaDon = "HD-" + ngayThangNam + String.format("%02d", soThuTu);

        return maHoaDon;
    }
}
