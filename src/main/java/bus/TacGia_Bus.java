package bus;

import dao.TacGia_Dao;
import entity.TacGia;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TacGia_Bus {

    private final TacGia_Dao tg_Dao;

    public TacGia_Bus() {
        tg_Dao = new TacGia_Dao();
    }
    
    public int getThuTuTacGia() throws SQLException {
        return tg_Dao.getThuTuTacGia();
    }
    
    public boolean themTacGia(TacGia tg) {
        return tg_Dao.themTacGia(tg);
    }
    
    public boolean updateTacGia(TacGia tg) {
        return tg_Dao.updateTacGia(tg);
    }
    
    public List<TacGia> getAllTacGia() {
        return tg_Dao.getAllTacGia();
    }
    
    public List<TacGia> timKiemTacGia(String queryParams) {
        return tg_Dao.timKiemTacGia(queryParams);
    }
}
