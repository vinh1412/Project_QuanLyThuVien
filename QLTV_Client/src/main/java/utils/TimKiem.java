package utils;

import entity.SanPham;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

class TimKiem {

    public static boolean kiemTraChuaDau(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).find();
    }

    public static String chuyenDoiKhongDau(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static ArrayList<SanPham> timSanPham(ArrayList<SanPham> list, String query) {
        ArrayList<SanPham> ketQua = new ArrayList<>();
        boolean coChuaDau = kiemTraChuaDau(query);
        if (coChuaDau) {
            for (SanPham sp : list) {
                String tenSanPham = sp.getTenSanPham();
                // Kiểm tra nếu query là từ có dấu thì tìm chính xác
                if (tenSanPham.toLowerCase().contains(query.toLowerCase())) {
                    ketQua.add(sp);
                }
            }
        }
        else {
            for (SanPham sp : list) {
                String tenSanPham = sp.getTenSanPham();
                // Kiểm tra nếu query là từ có dấu thì tìm chính xác
                if (chuyenDoiKhongDau(tenSanPham).toLowerCase().contains(query.toLowerCase())) {
                    ketQua.add(sp);
                }
            }
        }

        return ketQua;
    }
}
