package bus;

import dao.DanhMuc_Dao;
import entity.DanhMuc;
import java.util.ArrayList;
import java.util.List;

public class DanhMuc_Bus {
    private final DanhMuc_Dao danhMuc_Dao;
    public DanhMuc_Bus() {
        danhMuc_Dao = new DanhMuc_Dao();
    }
    
    public boolean themDanhMuc(String tenDanhMuc) {
        return danhMuc_Dao.themDanhMuc(tenDanhMuc);
    }
    
    public boolean updateDanhMuc(DanhMuc danhMuc) {
        return danhMuc_Dao.updateDanhMuc(danhMuc);
    }
    
    public List<DanhMuc> getAllDanhMuc() {
        return danhMuc_Dao.getAllDanhMuc();
    }
    
    public List<DanhMuc> timKiemDanhMuc(String queryParams) {
        return danhMuc_Dao.timKiemDanhMuc(queryParams);
    }
}
