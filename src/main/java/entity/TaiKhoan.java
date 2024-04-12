package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "TaiKhoan.findAll", query = "SELECT tk FROM TaiKhoan tk"),
        @NamedQuery(name = "TaiKhoan.findByTenTaiKhoan", query = "SELECT tk FROM TaiKhoan tk WHERE tk.tenTaiKhoan = :tenTaiKhoan"),
        @NamedQuery(name = "TaiKhoan.findByRole", query = "SELECT tk FROM TaiKhoan tk WHERE tk.role = :role")
})
public class TaiKhoan implements Serializable {
    @Id
    @Column(name = "TenTaiKhoan", columnDefinition = "varchar(255)")
    private String tenTaiKhoan;
    @Column(name = "MatKhau", columnDefinition = "varchar(255)")
    private String matKhau;
    @OneToOne
    @JoinColumn(name = "MaNhanVien", unique = true, nullable = false)
    private NhanVien nhanVien;
    @Column(name = "Role", columnDefinition = "nvarchar(50)")
    private String role;

    public TaiKhoan() {
    }

    public TaiKhoan(String tenTaiKhoan, String matKhau, NhanVien nhanVien, String role) {
        this.tenTaiKhoan = tenTaiKhoan;
        this.matKhau = matKhau;
        this.nhanVien = nhanVien;
        this.role = role;
    }

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.tenTaiKhoan);
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
        final TaiKhoan other = (TaiKhoan) obj;
        return Objects.equals(this.tenTaiKhoan, other.tenTaiKhoan);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "tenTaiKhoan='" + tenTaiKhoan + '\'' +
                ", matKhau='" + matKhau + '\'' +
                ", nhanVien=" + nhanVien +
                ", role='" + role + '\'' +
                '}';
    }
}
