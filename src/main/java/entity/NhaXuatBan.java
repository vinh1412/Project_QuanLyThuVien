package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "NhaXuatBan.findAll", query = "SELECT nxb FROM NhaXuatBan nxb"),
        @NamedQuery(name = "NhaXuatBan.find",query = "SELECT nxb FROM NhaXuatBan nxb WHERE (LOWER(nxb.maNhaXuatBan)) like LOWER(:maNXB) OR (LOWER(nxb.tenNhaXuatBan)) like LOWER(:tenNXB) OR nxb.soDienThoai like :sdt"),
        @NamedQuery(name = "NhaXuatBan.count", query = "SELECT COUNT(nxb) FROM NhaXuatBan nxb")
})
public class NhaXuatBan implements Serializable {
    @Id
    @Column(name = "MaNhaXuatBan", columnDefinition = "nchar(15)")
    private String maNhaXuatBan;
    @Column(name = "TenNhaXuatBan", columnDefinition = "nvarchar(255)")
    private String tenNhaXuatBan;
    @Column(name = "DiaChiNXB", columnDefinition = "nvarchar(255)")
    private String diaChiNXB;
    @Column(name = "SoDienThoai", columnDefinition = "varchar(20)")
    private String soDienThoai;

    @OneToMany(mappedBy = "nhaXuatBan", fetch = FetchType.EAGER)
    private Set<SanPham> sanPhams;
    public NhaXuatBan() {
    }

    public NhaXuatBan(String maNhaXuatBan) {
        this.maNhaXuatBan = maNhaXuatBan;
    }

    public NhaXuatBan(String maNhaXuatBan, String tenNhaXuatBan, String diaChiNXB, String soDienThoai) {
        this.maNhaXuatBan = maNhaXuatBan;
        this.tenNhaXuatBan = tenNhaXuatBan;
        this.diaChiNXB = diaChiNXB;
        this.soDienThoai = soDienThoai;
    }


    public String getMaNhaXuatBan() {
        return maNhaXuatBan;
    }

    public void setMaNhaXuatBan(String maNhaXuatBan) {
        this.maNhaXuatBan = maNhaXuatBan;
    }

    public String getTenNhaXuatBan() {
        return tenNhaXuatBan;
    }

    public void setTenNhaXuatBan(String tenNhaXuatBan) {
        this.tenNhaXuatBan = tenNhaXuatBan;
    }
    
    public String getDiaChiNXB() {
        return diaChiNXB;
    }

    public void setDiaChiNXB(String diaChiNXB) {
        this.diaChiNXB = diaChiNXB;
    }
    
    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public Set<SanPham> getSanPhams() {
        return sanPhams;
    }

    public void setSanPhams(Set<SanPham> sanPhams) {
        this.sanPhams = sanPhams;
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.maNhaXuatBan);
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
        final NhaXuatBan other = (NhaXuatBan) obj;
        return Objects.equals(this.maNhaXuatBan, other.maNhaXuatBan);
    }

    @Override
    public String toString() {
        return tenNhaXuatBan;
    }

}
