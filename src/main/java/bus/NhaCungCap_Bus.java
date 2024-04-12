package bus;

import dao.NhaCungCap_Dao;
import entity.NhaCungCap;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCap_Bus {
    private final NhaCungCap_Dao ncc_Dao;
    public NhaCungCap_Bus() {
        ncc_Dao = new NhaCungCap_Dao();
    }
    
    public int getThuTuNCC() throws SQLException {
        return ncc_Dao.getThuTuNCC();
    }
    
    public boolean themNCC(NhaCungCap ncc) {
        return ncc_Dao.themNCC(ncc);
    }
    
    public boolean updateNCC(NhaCungCap ncc) {
        return ncc_Dao.updateNCC(ncc);
    }
    
    public List<NhaCungCap> getAllNCC() {
        return ncc_Dao.getAllNCC();
    }
    
    public List<NhaCungCap> timKiemNCC(String queryParams) {
        return ncc_Dao.timKiemNCC(queryParams);
    }
}
