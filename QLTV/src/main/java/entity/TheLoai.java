package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "TheLoai.findAll", query = "SELECT tl FROM TheLoai tl"),
        @NamedQuery(name = "TheLoai.findByTenTheLoai", query = "SELECT tl FROM TheLoai tl WHERE tl.tenTheLoai like lower(:tenTheLoai)"),
        @NamedQuery(name = "TheLoai.existsById", query = "SELECT (count(tl)>0) FROM TheLoai tl WHERE tl.maTheLoai = :maTheLoai")
})
public class TheLoai implements Serializable {
    @Id
    @Column(name = "MaTheLoai", columnDefinition = "int")
    private int maTheLoai;
    @Column(name = "TenTheLoai", columnDefinition = "nvarchar(255)")
    private String tenTheLoai;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaDanhMuc")
    private DanhMuc danhMuc;

    @OneToMany(mappedBy = "theLoai", fetch = FetchType.EAGER)
    private Set<SanPham> sanPhams;
    public TheLoai() {
    }

    public TheLoai(int maTheLoai) {
        this.maTheLoai = maTheLoai;
    }

    public TheLoai(int maTheLoai, String tenTheLoai, DanhMuc danhMuc) {
        this.maTheLoai = maTheLoai;
        this.tenTheLoai = tenTheLoai;
        this.danhMuc = danhMuc;
    }

    public int getMaTheLoai() {
        return maTheLoai;
    }

    public void setMaTheLoai(int maTheLoai) {
        this.maTheLoai = maTheLoai;
    }

    public String getTenTheLoai() {
        return tenTheLoai;
    }

    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
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
        hash = 29 * hash + Objects.hashCode(this.maTheLoai);
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
        final TheLoai other = (TheLoai) obj;
        return Objects.equals(this.maTheLoai, other.maTheLoai);
    }

    @Override
    public String toString() {
        return tenTheLoai;
    }
    
}
