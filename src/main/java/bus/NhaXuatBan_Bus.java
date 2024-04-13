package bus;

import dao.NhaXuatBan_Dao;
import entity.NhaXuatBan;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhaXuatBan_Bus {

    private final NhaXuatBan_Dao nxb_Dao;

    public NhaXuatBan_Bus() {
        nxb_Dao = new NhaXuatBan_Dao();
    }
    
    public int getThuTuNXB() throws SQLException {
        return nxb_Dao.getThuTuNXB();
    }
    
    public boolean themNXB(NhaXuatBan nxb) {
        return nxb_Dao.themNXB(nxb);
    }
    
    public boolean updateNXB(NhaXuatBan nxb) {
        return nxb_Dao.updateNXB(nxb);
    }
    
    public List<NhaXuatBan> getAllNXB() {
        return nxb_Dao.getAllNXB();
    }
    
    public List<NhaXuatBan> timKiemNXB(String queryParams) {
        return nxb_Dao.timKiemNXB(queryParams);
    }
}
