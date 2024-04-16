package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "DanhMucSanPham")
@NamedQueries({
        @NamedQuery(name = "DanhMuc.findAll", query = "SELECT d FROM DanhMuc d"),
        @NamedQuery(name = "DanhMuc.findByTenDanhMuc", query = "SELECT d FROM DanhMuc d WHERE d.tenDanhMuc like lower(:tenDanhMuc) "),
})
public class DanhMuc implements Serializable {
    @Id
    @Column(name = "MaDanhMuc", columnDefinition = "int")
    private int maDanhMuc;
    @Column(name = "TenDanhMuc", columnDefinition = "nvarchar(255)")
    private String tenDanhMuc;

    @OneToMany(mappedBy = "danhMuc", fetch = FetchType.EAGER)
    private Set<TheLoai> theLoais;
    public DanhMuc() {
    }

    public DanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public DanhMuc(int maDanhMuc, String tenDanhMuc) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
    }


    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public Set<TheLoai> getTheLoais() {
        return theLoais;
    }

    public void setTheLoais(Set<TheLoai> theLoais) {
        this.theLoais = theLoais;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.maDanhMuc);
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
        final DanhMuc other = (DanhMuc) obj;
        return Objects.equals(this.maDanhMuc, other.maDanhMuc);
    }

    @Override
    public String toString() {
        return tenDanhMuc;
    }

}
