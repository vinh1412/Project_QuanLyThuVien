package entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
public class KhachHang {
    @Id
    @Column(name = "MaKhachHang", columnDefinition = "nchar(15)")
    private String maKhachHang;
    @Column(name = "TenKhachHang", columnDefinition = "nvarchar(255)")
    private String tenKhachHang;
    @Column(name = "GioiTinh", columnDefinition = "int")
    private int gioiTinh;
    @Column(name = "SoDienThoai", columnDefinition = "varchar(20)")
    private String soDienThoai;
    @Column(name = "NgaySinh", columnDefinition = "date")
    private Date ngaySinh;
    @Column(name = "DiemDoiThuong", columnDefinition = "int")
    private int diemDoiThuong;

    //Tạo quan hệ 1-n với bảng HoaDon
    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private Set<HoaDon> hoaDons;
    public KhachHang() {
    }
    public KhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public KhachHang(String maKhachHang, String tenKhachHang, int gioiTinh, String soDienThoai, Date ngaySinh, int diemDoiThuong) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.diemDoiThuong = diemDoiThuong;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public int getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(int gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public int getDiemDoiThuong() {
        return diemDoiThuong;
    }

    public void setDiemDoiThuong(int diemDoiThuong) {
        this.diemDoiThuong = diemDoiThuong;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public Set<HoaDon> getHoaDons() {
        return hoaDons;
    }
    public void setHoaDons(Set<HoaDon> hoaDons) {
        this.hoaDons = hoaDons;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.maKhachHang);
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
        final KhachHang other = (KhachHang) obj;
        return Objects.equals(this.maKhachHang, other.maKhachHang);
    }

    @Override
    public String toString() {
        return "KhachHang{" + "maKhachHang=" + maKhachHang + ", tenKhachHang=" + tenKhachHang + ", gioiTinh=" + gioiTinh + ", soDienThoai=" + soDienThoai +  ", ngaySinh=" + ngaySinh + ", diaChi=";
    }
    
    
    
    
}
