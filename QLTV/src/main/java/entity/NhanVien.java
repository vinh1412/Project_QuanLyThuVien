package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "NhanVien.findAll", query = "SELECT nv FROM NhanVien nv"),
        @NamedQuery(name = "NhanVien.findNV", query = "SELECT nv FROM NhanVien nv WHERE nv.maNhanVien like lower(:maNhanVien) or nv.tenNhanVien like lower(:tenNhanVien) or nv.soDienThoai like lower(:soDienThoai) or nv.luongCoBan = :luongCoBan"),
        @NamedQuery(name="NhanVien.checkSDT", query = "SELECT (count(nv)) FROM NhanVien nv WHERE nv.soDienThoai = :soDienThoai"),
        @NamedQuery(name="NhanVien.countNV", query = "SELECT (count(nv)) FROM NhanVien nv"),
        @NamedQuery(name="NhanVien.findNVByMaNV", query = "SELECT nv FROM NhanVien nv WHERE nv.maNhanVien = :maNhanVien"),
})
public class NhanVien implements Serializable {
    @Id
    @Column(name = "MaNhanVien", columnDefinition = "nchar(15)")
    private String maNhanVien;
    @Column(name = "TenNhanVien", columnDefinition = "nvarchar(255)")
    private String tenNhanVien;
    @Column(name = "NgaySinh", columnDefinition = "date")
    private Date ngaySinh;
    @Column(name = "GioiTinh", columnDefinition = "int")
    private int gioiTinh;
    @Column(name = "SoDienThoai", columnDefinition = "varchar(20)")
    private String soDienThoai;
    @Column(name = "DiaChi", columnDefinition = "nvarchar(255)")
    private String diaChi;
    @Column(name = "TrangThai", columnDefinition = "int")
    private int trangThai;
    @Column(name = "ChucVu", columnDefinition = "int")
    private int chucVu;
    @Column(name = "LuongCoBan", columnDefinition = "money")
    private double luongCoBan;
    @Column(name = "NgayVaoLam", columnDefinition = "date")
    private Date ngayVaoLam;

    //Tạo quan hệ 1-n với bảng HoaDon
    @OneToMany(mappedBy = "nhanVien", fetch = FetchType.EAGER)
    private Set<HoaDon> hoaDons;
    public NhanVien() {
    }

    public NhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String maNhanVien, String tenNhanVien, Date ngaySinh, int gioiTinh, String soDienThoai, String diaChi, int trangThai, int chucVu, double luongCoBan, Date ngayVaoLam) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
        this.chucVu = chucVu;
        this.luongCoBan = luongCoBan;
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
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

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getChucVu() {
        return chucVu;
    }

    public void setChucVu(int chucVu) {
        this.chucVu = chucVu;
    }

    public double getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(double luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public Date getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(Date ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public Set<HoaDon> getHoaDons() {
        return hoaDons;
    }

    public void setHoaDons(Set<HoaDon> hoaDons) {
        this.hoaDons = hoaDons;
    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.maNhanVien);
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
        final NhanVien other = (NhanVien) obj;
        return Objects.equals(this.maNhanVien, other.maNhanVien);
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien='" + maNhanVien + '\'' +
                ", tenNhanVien='" + tenNhanVien + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", gioiTinh=" + gioiTinh +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", trangThai=" + trangThai +
                ", chucVu=" + chucVu +
                ", luongCoBan=" + luongCoBan +
                ", ngayVaoLam=" + ngayVaoLam +
                '}';
    }
}
