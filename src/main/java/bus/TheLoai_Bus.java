package bus;

import dao.TheLoai_Dao;
import entity.TheLoai;
import java.util.ArrayList;
import java.util.List;

public class TheLoai_Bus {
     private final TheLoai_Dao theLoai_Dao;
    public TheLoai_Bus() {
        theLoai_Dao = new TheLoai_Dao();
    }
    
    public boolean themTheLoai(String tenDanhMuc, int maDanhMuc) {
        return theLoai_Dao.themTheLoai(tenDanhMuc, maDanhMuc);
    }
    
    public boolean updateTheLoai(TheLoai theLoai) {
        return theLoai_Dao.updateTheLoai(theLoai);
    }
    
    public List<TheLoai> getAllTheLoai() {
        return theLoai_Dao.getAllTheLoai();
    }
    
    public List<TheLoai> timKiemTheLoai(String queryParams) {
        return theLoai_Dao.timKiemTheLoai(queryParams);
    }
}
