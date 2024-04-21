package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "HoaDon.getThuTuHoaDon", query = "select count(hd) from HoaDon hd"),
        @NamedQuery(name = "HoaDon.getAllHoaDon", query = "select hd from HoaDon hd"),
        @NamedQuery(name = "HoaDon.getAllHDByNhanVien", query = "select hd from HoaDon hd where hd.nhanVien.maNhanVien=:maNhanVien"),
        @NamedQuery(name = "HoaDon.findHoaDon", query = "SELECT hd FROM HoaDon hd JOIN hd.nhanVien nv WHERE LOWER(hd.maHoaDon) LIKE LOWER(:query) AND (LOWER(hd.nhanVien.maNhanVien) LIKE LOWER(:maNV) OR LOWER(nv.tenNhanVien) LIKE LOWER(:query))")
})
public class HoaDon implements Serializable {
    @Id
    @Column(name = "MaHoaDon", columnDefinition = "nchar(15)")
    private String maHoaDon;
    @Column(name = "NgayLap", columnDefinition = "date")
    private Date ngayLap;

    //Tạo quan hệ n-1 với bảng KhachHang
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaKhachHang")
    private KhachHang khachHang;

    //Tạo quan hệ n-1 với bảng NhanVien
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNhanVien")
    private NhanVien nhanVien;
    @Column(name = "GiamGia", columnDefinition = "float")
    private double giamGia;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.EAGER)
    private Set<ChiTietHoaDon> chiTietHoaDons;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }



    public HoaDon(String maHoaDon, Date ngayLap, KhachHang khachHang, NhanVien nhanVien, double giamGia) {
        this.maHoaDon = maHoaDon;
        this.ngayLap = ngayLap;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.giamGia = giamGia;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public Date getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Date ngayLap) {
        this.ngayLap = ngayLap;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }
    
    public double getGiamGia () {
        return this.giamGia;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Set<ChiTietHoaDon> getChiTietHoaDons() {
        return chiTietHoaDons;
    }

    public void setChiTietHoaDons(Set<ChiTietHoaDon> chiTietHoaDons) {
        this.chiTietHoaDons = chiTietHoaDons;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.maHoaDon);
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
        final HoaDon other = (HoaDon) obj;
        return Objects.equals(this.maHoaDon, other.maHoaDon);
    }

    @Override
    public String toString() {
        return "HoaDon{" + "maHoaDon=" + maHoaDon + ", ngayLap=" + ngayLap + ", khachHang=" + khachHang + ", nhanVien=" + nhanVien + '}';
    }

}
