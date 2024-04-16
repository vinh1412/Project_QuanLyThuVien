package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "TacGia.findAll", query = "SELECT tg FROM TacGia tg"),
        @NamedQuery(name = "TacGia.find",query = "SELECT tg FROM TacGia tg WHERE (LOWER(tg.maTacGia)) like LOWER(:maTG) OR (LOWER(tg.tenTacGia)) like LOWER(:tenTG) OR tg.soDienThoai like :sdt"),
        @NamedQuery(name = "TacGia.count", query = "SELECT COUNT(tg) FROM TacGia tg")
})
public class TacGia implements Serializable {
    @Id
    @Column(name = "MaTacGia", columnDefinition = "nchar(15)")
    private String maTacGia;
    @Column(name = "TenTacGia", columnDefinition = "nvarchar(255)")
    private String tenTacGia;
    @Column(name = "SoDienThoai", columnDefinition = "varchar(20)")
    private String soDienThoai;
    @Column(name = "GioiTinh", columnDefinition = "int")
    private int gioiTinh;

    @OneToMany(mappedBy = "tacGia", fetch = FetchType.EAGER)
    private Set<SanPham> sanPhams;
    public TacGia() {
    }

    public TacGia(String maTacGia) {
        this.maTacGia = maTacGia;
    }

    public TacGia(String maTacGia, String tenTacGia, String soDienThoai, int gioiTinh) {
        this.maTacGia = maTacGia;
        this.tenTacGia = tenTacGia;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
    }

    public String getMaTacGia() {
        return maTacGia;
    }

    public void setMaTacGia(String maTacGia) {
        this.maTacGia = maTacGia;
    }

    public String getTenTacGia() {
        return tenTacGia;
    }

    public void setTenTacGia(String tenTacGia) {
        this.tenTacGia = tenTacGia;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public int getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(int gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Set<SanPham> getSanPhams() {
        return sanPhams;
    }

    public void setSanPhams(Set<SanPham> sanPhams) {
        this.sanPhams = sanPhams;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.maTacGia);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TacGia other = (TacGia) obj;
        return Objects.equals(this.maTacGia, other.maTacGia);
    }

    @Override
    public String toString() {
        return tenTacGia;
    }
}
