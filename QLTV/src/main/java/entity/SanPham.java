package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "SanPham.findAll", query = "SELECT sp FROM SanPham sp"),
//        @NamedQuery(name = "SanPham.findAll", query = "SELECT sp FROM SanPham sp JOIN NhaCungCap n ON sp.nhaCungCap.maNhaCungCap = n.maNhaCungCap JOIN TheLoai tl ON sp.theLoai.maTheLoai = tl.maTheLoai JOIN DanhMuc d ON tl.danhMuc.maDanhMuc = d.maDanhMuc"),
//        @NamedQuery(name = "SanPham.findSPByMaSanPham", query = "SELECT sp FROM SanPham sp WHERE sp.maSanPham = :maSanPham"),
        @NamedQuery(name = "SanPham.find",query = "SELECT sp FROM SanPham sp JOIN NhaCungCap n ON sp.nhaCungCap.maNhaCungCap = n.maNhaCungCap JOIN TheLoai tl ON sp.theLoai.maTheLoai = tl.maTheLoai JOIN DanhMuc d ON tl.danhMuc.maDanhMuc = d.maDanhMuc WHERE lower(sp.tenSanPham) like lower(:tenSanPham) or lower(sp.maSanPham) like lower(:maSanPham) "),
        @NamedQuery(name = "SanPham.count",query = "SELECT COUNT(sp) FROM SanPham sp"),
        @NamedQuery(name=   "SanPham.getSPByMaSP",query = "SELECT sp FROM SanPham sp JOIN NhaCungCap n ON sp.nhaCungCap.maNhaCungCap = n.maNhaCungCap JOIN TheLoai tl ON sp.theLoai.maTheLoai = tl.maTheLoai JOIN DanhMuc d ON tl.danhMuc.maDanhMuc = d.maDanhMuc WHERE sp.maSanPham = :maSanPham"),
})
public class SanPham implements Serializable {
    @Id
    @Column(name = "MaSanPham", columnDefinition = "nchar(15)")
    private String maSanPham;
    @Column(name = "TenSanPham", columnDefinition = "nvarchar(255)")
    private String tenSanPham;
    @Column(name = "GiaMua", columnDefinition = "float")
    private double giaMua;
    @Column(name = "HinhAnh", columnDefinition = "varchar(255)")
    private String hinhAnh;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNhaCungCap")
    private NhaCungCap nhaCungCap;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTacGia")
    private TacGia tacGia;
    @Column(name = "SoTrang", columnDefinition = "int")
    private int soTrang;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTheLoai")
    private TheLoai theLoai;
    @Column(name = "MoTa", columnDefinition = "nvarchar(255)")
    private String moTaSanPham;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNXB")
    private NhaXuatBan nhaXuatBan;
    @Column(name = "SoLuongTon", columnDefinition = "int")
    private int soLuongTon;
    @Column(name = "VAT", columnDefinition = "float")
    private double vat;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.EAGER)
    private Set<ChiTietHoaDon> chiTietHoaDons;
    public SanPham() {
    }

    public SanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public SanPham(String maSanPham, String tenSanPham, double giaMua, int soLuongTon, double vat) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaMua = giaMua;
        this.soLuongTon = soLuongTon;
        this.vat = vat;
    }

    public SanPham(String maSanPham, String tenSanPham, double giaMua, String hinhAnh, NhaCungCap nhaCungCap, TacGia tacGia, int soTrang, TheLoai theLoai, String moTaSanPham, NhaXuatBan nhaXuatBan, int soLuongTon, double vat) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaMua = giaMua;
        this.hinhAnh = hinhAnh;
        this.nhaCungCap = nhaCungCap;
        this.tacGia = tacGia;
        this.soTrang = soTrang;
        this.theLoai = theLoai;
        this.moTaSanPham = moTaSanPham;
        this.nhaXuatBan = nhaXuatBan;
        this.soLuongTon = soLuongTon;
        this.vat = vat;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public double getGiaMua() {
        return giaMua;
    }

    public void setGiaMua(double giaMua) {
        this.giaMua = giaMua;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public TacGia getTacGia() {
        return tacGia;
    }

    public void setTacGia(TacGia tacGia) {
        this.tacGia = tacGia;
    }

    public int getSoTrang() {
        return soTrang;
    }

    public void setSoTrang(int soTrang) {
        this.soTrang = soTrang;
    }

    public TheLoai getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(TheLoai theLoai) {
        this.theLoai = theLoai;
    }

    public String getMoTaSanPham() {
        return moTaSanPham;
    }

    public void setMoTaSanPham(String moTaSanPham) {
        this.moTaSanPham = moTaSanPham;
    }

    public NhaXuatBan getNhaXuatBan() {
        return nhaXuatBan;
    }

    public void setNhaXuatBan(NhaXuatBan nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public Set<ChiTietHoaDon> getChiTietHoaDons() {
        return chiTietHoaDons;
    }

    public void setChiTietHoaDons(Set<ChiTietHoaDon> chiTietHoaDons) {
        this.chiTietHoaDons = chiTietHoaDons;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.maSanPham);
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
        final SanPham other = (SanPham) obj;
        return Objects.equals(this.maSanPham, other.maSanPham);
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maSanPham='" + maSanPham + '\'' +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", giaMua=" + giaMua +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", nhaCungCap=" + nhaCungCap +
                ", tacGia=" + tacGia +
                ", soTrang=" + soTrang +
                ", theLoai=" + theLoai +
                ", moTaSanPham='" + moTaSanPham + '\'' +
                ", nhaXuatBan=" + nhaXuatBan +
                ", soLuongTon=" + soLuongTon +
                ", vat=" + vat +
                '}';
    }
}
