/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views;

import bus.NhanVien_Bus;
import bus.TaiKhoan_Bus;
import entity.NhanVien;
import utils.IconUtils;
import utils.NotifyToast;
import utils.RMIServiceURL;
import utils.getCommonIcons;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class panel_QuanLyNhanVien extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private final NhanVien_Bus nhanVien_Bus;
    private final DefaultTableModel model;
    private Timer debounce;

    public panel_QuanLyNhanVien() throws RemoteException, MalformedURLException, NotBoundException {
        initComponents();
        customInitComponents();
        nhanVien_Bus = (NhanVien_Bus) Naming.lookup(URL + "NhanVien");
        model = (DefaultTableModel) table_dsNV.getModel();
        DocDuLieuTuSQLVaoTable();
    }

    private void customInitComponents() {
        txt_timKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm Kiếm...");
        btn_them.setIcon(IconUtils.addIcon());
        btn_capNhat.setIcon(IconUtils.updateIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
        btn_loc.setIcon(IconUtils.filterIcon());
        cld_ngayVaoLam.setEnabled(false);
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            try {
                timNhanVien();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    public void timNhanVien() throws RemoteException {
        List<NhanVien> dsNV = nhanVien_Bus.timNhanVien(txt_timKiem.getText().trim());
        model.setRowCount(0);
        for (NhanVien nv : dsNV) {
            model.addRow(getObjectNV(nv));
        }

    }

    public boolean valiData() throws RemoteException {
        String tenNV = txt_tenNV.getText().trim();
        String soDT = txt_soDienThoai.getText().trim();
        String luongCoBan = txt_luongCoBan.getText().trim();
        Date ngaySinhDate = cld_ngaySinhNV.getDate();
        LocalDate ngaySinh = ngaySinhDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayHienTai = LocalDate.now();
        Period period = Period.between(ngaySinh, ngayHienTai);
        int tuoi = period.getYears();

        if (tenNV.isEmpty() || tenNV.matches("^[A-Za-z\\p{L}  ]+$") == false) {
            NotifyToast.showErrorToast("Tên nhân viên không hợp lệ");
            txt_tenNV.requestFocus();
            return false;
        }

        if (tuoi < 18) {
            NotifyToast.showErrorToast("Nhân viên phải >= 18 tuổi");
            return false;
        }

        if (!soDT.matches("(^(03)[2-9]\\d{7})|(^(07)[06-9]\\d{7})|(^(08)[1-5]\\d{7})|(^(056)\\d{7})|(^(058)\\d{7})|(^(059)\\d{7})|(^(09)[0-46-9]\\d{7})")) {
            NotifyToast.showErrorToast("Số điện thoại không hợp lệ");
            txt_soDienThoai.requestFocus();
            return false;
        }
        
//        if (nhanVien_Bus.checkSDT(soDT) > 0) {
//            NotifyToast.showErrorToast("Số điện thoại đã tồn tại");
//            txt_soDienThoai.requestFocus();
//            return false;
//        }

        if (txt_diaChi.getText().equals("")) {
            NotifyToast.showErrorToast("Địa chỉ không được để trống");
            txt_diaChi.requestFocus();
            return false;
        }
        
        if (luongCoBan.equals("")) {
            NotifyToast.showErrorToast("Lương cơ bản không được để trống");
            txt_luongCoBan.requestFocus();
            return false;
        }
        
        if (Float.parseFloat(luongCoBan) < 500000) {
            NotifyToast.showErrorToast("Lương cơ bản phải >= 500.000 VND");
            txt_luongCoBan.requestFocus();
            return false;
        }

        return true;
    }

    //Cap Nhat Nhân Viên
    private void capNhatNhanVien() throws RemoteException {
        int r = table_dsNV.getSelectedRow();
        if (r < 0) {
            NotifyToast.showErrorToast("Chọn nhân viên cần sửa thông tin");
            return;
        }
        if (valiData()) {
            NhanVien nv = taoNhanVien();

            if (nhanVien_Bus.capNhatNV(nv)) {
                model.removeRow(r); //xoa hang duoc chon
                model.insertRow(r, getObjectNV(nv));
                NotifyToast.showSuccessToast("Cập nhật nhân viên thành công");
                XoaTrang();
            } else {
                NotifyToast.showErrorToast("Cập nhật nhân viên thất bại");
            }
        }

    }

    //Tạo Nhân Viên
    public NhanVien taoNhanVien() {
        String maNV = txt_maNV.getText();
        String tenNV = txt_tenNV.getText();
        Date ngaySinh = cld_ngaySinhNV.getDate();
        int gioiTinh = cmb_gioiTinh.getSelectedIndex();
        String soDT = txt_soDienThoai.getText();
        String diaChi = txt_diaChi.getText();
        int trangThai = cmb_trangThai.getSelectedIndex();
        int chucVu = cmb_chucVu.getSelectedIndex();
        String luongCoBan = txt_luongCoBan.getText();
        Date ngayVaoLam = cld_ngayVaoLam.getDate();
        double luong = Double.parseDouble(luongCoBan);
        NhanVien nhanVien = new NhanVien(maNV, tenNV, ngaySinh, gioiTinh, soDT, diaChi, trangThai, chucVu, luong, ngayVaoLam);
        return nhanVien;
    }

    //Them Nhan Vien
    public void themNhanVien() throws RemoteException, MalformedURLException, NotBoundException {
        String maNV = generateCustomerCode();
        String tenNV = txt_tenNV.getText();
        Date ngaySinh = cld_ngaySinhNV.getDate();
        int gioiTinh = cmb_gioiTinh.getSelectedIndex();
        String soDT = txt_soDienThoai.getText();
        String diaChi = txt_diaChi.getText();
        int trangThai = cmb_trangThai.getSelectedIndex();
        int chucVu = cmb_chucVu.getSelectedIndex();
        String luongCoBan = txt_luongCoBan.getText();
        double luong = Double.parseDouble(luongCoBan);
        Date ngayVaoLam = cld_ngayVaoLam.getDate();
        NhanVien nhanVien = new NhanVien(maNV, tenNV, ngaySinh, gioiTinh, soDT, diaChi, trangThai, chucVu, luong, ngayVaoLam);
        boolean themNhanVien = nhanVien_Bus.themNhanVien(nhanVien);
        if (!txt_maNV.getText().equals("")) {
            NotifyToast.showErrorToast("Nhân viên đã tồn tại");
            return;
        }
        if (nhanVien_Bus.checkSDT(soDT) > 0) {
            NotifyToast.showErrorToast("Số điện thoại đã tồn tại");
            txt_soDienThoai.requestFocus();
            return;
        }
        if (themNhanVien) {
            XoaHetDuLieuTrenTable();
            DocDuLieuTuSQLVaoTable();
            XoaTrang();
            NotifyToast.showSuccessToast("Thêm nhân viên thành công");
            TaiKhoan_Bus tk_Bus = (TaiKhoan_Bus) Naming.lookup(URL + "TaiKhoan");
            tk_Bus.themTaiKhoan(nhanVien);
        } else {
            NotifyToast.showSuccessToast("Thêm nhân viên thất bại");
        }

    }

    //=====================//
    public String generateCustomerCode() throws RemoteException {
        // Lấy năm sinh từ ngày được chọn
        Date ngaySinh = cld_ngaySinhNV.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        String namSinhh = sdf.format(ngaySinh);

        String soCuoi = String.format("%02d", nhanVien_Bus.getSoLuongNV() + 1);
        String customerCode = "NV-" + namSinhh + soCuoi;

        return customerCode;
    }

    //========================================//
    public Object[] getObjectNV(NhanVien nv) {
        String gt = "";
        if (nv.getGioiTinh() == 0) {
            gt = "Nam";
        } else if (nv.getGioiTinh() == 1) {
            gt = "Nữ";
        }

        String trangThai;
        if (nv.getTrangThai() == 0) {
            trangThai = "Đang làm";
        } else {
            trangThai = "Nghỉ Việc";
        }

        String chucVu;
        if (nv.getChucVu() == 1) {
            chucVu = "Nhân Viên Quản lý";
        } else {
            chucVu = "Nhân Viên Bán hàng";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String ngaySinh = dateFormat.format(nv.getNgaySinh());
        String ngayVaoLam = dateFormat.format(nv.getNgayVaoLam());

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount = decimalFormat.format(nv.getLuongCoBan());

        formattedAmount += " VNĐ";

        Object[] obj = {nv.getMaNhanVien(), nv.getTenNhanVien(), gt, nv.getDiaChi(), nv.getSoDienThoai(), ngaySinh, chucVu, formattedAmount, trangThai, ngayVaoLam};

        return obj;
    }

    //Doc Du Lieu Tu SQL vao Table
    private void DocDuLieuTuSQLVaoTable() throws RemoteException {
        List<NhanVien> dsNV = nhanVien_Bus.getAllNhanVien();
        model.setRowCount(0);
        for (NhanVien nv : dsNV) {
            model.addRow(getObjectNV(nv));
        }
    }

    // Xoa Du Lieu Tren Table
    public void XoaHetDuLieuTrenTable() {
        model.setRowCount(0);
    }

    //Xoa Trang
    public void XoaTrang() {
        txt_maNV.setText("");
        txt_tenNV.setText("");
        txt_soDienThoai.setText("");
        txt_diaChi.setText("");
        txt_luongCoBan.setText("");
        txt_timKiem.setText("");
        cmb_locTrangThai.setSelectedIndex(2);
        cmb_locChucVu.setSelectedIndex(2);
        cmb_locGioiTinh.setSelectedIndex(3);
        cld_ngaySinhNV.setDate(new Date());
        cld_ngayVaoLam.setDate(new Date());
    }

    //Làm mới
    public void LamMoi() throws RemoteException {
        XoaTrang();
        XoaHetDuLieuTrenTable();
        DocDuLieuTuSQLVaoTable();
    }

    // lọc theo chức vụ
    public void locChucVu() throws RemoteException {
        int chucVu = cmb_locChucVu.getSelectedIndex();
        int gioiTinh = cmb_locGioiTinh.getSelectedIndex();
        int trangThai = cmb_locTrangThai.getSelectedIndex();

        List<NhanVien> dsNV = new ArrayList<>();

        if (chucVu != 2 && gioiTinh != 3 && trangThai != 2) {
            dsNV = nhanVien_Bus.LayDanhSachNhanVienTheoTieuChi(chucVu, gioiTinh, trangThai);
        }
        if (chucVu != 2 && gioiTinh == 3 && trangThai == 2) {
            dsNV = nhanVien_Bus.LayDanhSachNhanVienTheoTieuChi(chucVu, 3, 2);
        }
        if (chucVu == 2 && gioiTinh != 3 && trangThai == 2) {
            dsNV = nhanVien_Bus.LayDanhSachNhanVienTheoTieuChi(2, gioiTinh, 2);
        }
        if (chucVu == 2 && gioiTinh == 3 && trangThai != 2) {
            dsNV = nhanVien_Bus.LayDanhSachNhanVienTheoTieuChi(2, 3, trangThai);
        }
        if (chucVu != 2 && gioiTinh != 3 && trangThai == 2) {
            dsNV = nhanVien_Bus.LayDanhSachNhanVienTheoTieuChi(chucVu, gioiTinh, 2);
        }
        if (chucVu != 2 && gioiTinh == 3 && trangThai != 2) {
            dsNV = nhanVien_Bus.LayDanhSachNhanVienTheoTieuChi(chucVu, 3, trangThai);
        }
        if (chucVu == 2 && gioiTinh != 3 && trangThai != 2) {
            dsNV = nhanVien_Bus.LayDanhSachNhanVienTheoTieuChi(2, gioiTinh, trangThai);
        }
        if (chucVu == 2 && gioiTinh == 3 && trangThai == 2) {
            dsNV = nhanVien_Bus.getAllNhanVien();
        }
        XoaHetDuLieuTrenTable();
        int stt = 0;
        for (NhanVien nv : dsNV) {
            model.addRow(getObjectNV(nv));
            stt++;
        }
        if (stt == 0) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy nhân viên");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scr_main = new javax.swing.JScrollPane();
        table_dsNV = new javax.swing.JTable();
        pnl_south = new javax.swing.JPanel();
        txt_timKiem = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_capNhat = new javax.swing.JButton();
        btn_lamMoi = new javax.swing.JButton();
        lbl_locChucVu = new javax.swing.JLabel();
        cmb_locChucVu = new javax.swing.JComboBox<>();
        lbl_locGioiTinh = new javax.swing.JLabel();
        cmb_locGioiTinh = new javax.swing.JComboBox<>();
        lbl_locTrangThai = new javax.swing.JLabel();
        cmb_locTrangThai = new javax.swing.JComboBox<>();
        btn_loc = new javax.swing.JButton();
        pnl_center = new javax.swing.JPanel();
        lbl_maNV = new javax.swing.JLabel();
        txt_maNV = new javax.swing.JTextField();
        lbl_tenNV = new javax.swing.JLabel();
        txt_tenNV = new javax.swing.JTextField();
        lbl_gioiTinh = new javax.swing.JLabel();
        cmb_gioiTinh = new javax.swing.JComboBox<>();
        lbl_diaChi = new javax.swing.JLabel();
        txt_diaChi = new javax.swing.JTextField();
        lbl_soDienThoai = new javax.swing.JLabel();
        txt_soDienThoai = new javax.swing.JTextField();
        lbl_chucVu = new javax.swing.JLabel();
        lbl_trangThai = new javax.swing.JLabel();
        lbl_ngaySinh = new javax.swing.JLabel();
        cmb_chucVu = new javax.swing.JComboBox<>();
        lbl_luongCoBan = new javax.swing.JLabel();
        txt_luongCoBan = new javax.swing.JTextField();
        cmb_trangThai = new javax.swing.JComboBox<>();
        lbl_ngayVaoLam = new javax.swing.JLabel();
        cld_ngaySinhNV = new com.toedter.calendar.JDateChooser();
        cld_ngayVaoLam = new com.toedter.calendar.JDateChooser();
        pnl_top = new javax.swing.JPanel();
        lbl_tieuDe = new javax.swing.JLabel();

        setBackground(new java.awt.Color(240, 240, 240));

        scr_main.setBackground(new java.awt.Color(255, 255, 255));
        scr_main.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách nhân viên", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        scr_main.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scr_main.setPreferredSize(new java.awt.Dimension(1420, 600));

        table_dsNV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_dsNV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Nhân Viên", "Tên Nhân Viên", "Giới Tính", "Địa Chỉ", "Số Điện Thoại", "Ngày Sinh", "Chức Vụ", "Lương Cơ Bản", "Trạng Thái", "Ngày Vào Làm"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_dsNV.setAutoscrolls(false);
        table_dsNV.setRowHeight(32);
        table_dsNV.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_dsNVMouseClicked(evt);
            }
        });
        scr_main.setViewportView(table_dsNV);

        pnl_south.setBackground(new java.awt.Color(255, 255, 255));
        pnl_south.setPreferredSize(new java.awt.Dimension(1520, 80));

        txt_timKiem.setPreferredSize(new java.awt.Dimension(68, 35));
        txt_timKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_timKiemActionPerformed(evt);
            }
        });
        txt_timKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_timKiemKeyPressed(evt);
            }
        });

        btn_them.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_them.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_them.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themActionPerformed(evt);
            }
        });

        btn_capNhat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_capNhat.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_capNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_capNhatActionPerformed(evt);
            }
        });

        btn_lamMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_lamMoi.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_lamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lamMoiActionPerformed(evt);
            }
        });

        lbl_locChucVu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_locChucVu.setText("Chức Vụ:");

        cmb_locChucVu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_locChucVu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nhân Viên Quản Lý", "Nhân Viên Bán Hàng", "Tất cả" }));
        cmb_locChucVu.setSelectedIndex(2);
        cmb_locChucVu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_locChucVuActionPerformed(evt);
            }
        });

        lbl_locGioiTinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_locGioiTinh.setText("Giới  Tính:");

        cmb_locGioiTinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ", "Khác", "Tất cả" }));
        cmb_locGioiTinh.setSelectedIndex(3);
        cmb_locGioiTinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_locGioiTinhActionPerformed(evt);
            }
        });

        lbl_locTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_locTrangThai.setText("Trạng Thái:");

        cmb_locTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_locTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang Làm", "Nghỉ Việc", "Tất cả" }));
        cmb_locTrangThai.setSelectedIndex(2);
        cmb_locTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_locTrangThaiActionPerformed(evt);
            }
        });

        btn_loc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_loc.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_loc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_locActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_southLayout = new javax.swing.GroupLayout(pnl_south);
        pnl_south.setLayout(pnl_southLayout);
        pnl_southLayout.setHorizontalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_southLayout.createSequentialGroup()
                        .addComponent(lbl_locChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cmb_locChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98)
                        .addComponent(lbl_locGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmb_locGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 90, 90)
                        .addComponent(lbl_locTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_locTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE))
                    .addGroup(pnl_southLayout.createSequentialGroup()
                        .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_loc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnl_southLayout.setVerticalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_loc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbl_locChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmb_locChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_locGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmb_locGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_locTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmb_locTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
        );

        pnl_center.setBackground(new java.awt.Color(255, 255, 255));
        pnl_center.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Nhân Viên", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pnl_center.setMaximumSize(new java.awt.Dimension(52767, 32767));
        pnl_center.setPreferredSize(new java.awt.Dimension(1520, 100));

        lbl_maNV.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_maNV.setText("Mã nhân viên:");

        txt_maNV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_maNV.setEnabled(false);
        txt_maNV.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_maNV.setPreferredSize(new java.awt.Dimension(0, 30));

        lbl_tenNV.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tenNV.setText("Tên nhân viên:");

        txt_tenNV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tenNV.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_tenNV.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_tenNV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tenNVActionPerformed(evt);
            }
        });

        lbl_gioiTinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_gioiTinh.setText("Giới tính:");

        cmb_gioiTinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ", "Khác" }));
        cmb_gioiTinh.setPreferredSize(new java.awt.Dimension(0, 30));

        lbl_diaChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_diaChi.setText("Địa chỉ:");

        txt_diaChi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_diaChi.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_diaChi.setPreferredSize(new java.awt.Dimension(0, 30));

        lbl_soDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_soDienThoai.setText("Số điện thoại:");

        txt_soDienThoai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_soDienThoai.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_soDienThoai.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_soDienThoai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_soDienThoaiKeyTyped(evt);
            }
        });

        lbl_chucVu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_chucVu.setText("Chức Vụ:");

        lbl_trangThai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_trangThai.setText("Trạng Thái:");

        lbl_ngaySinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_ngaySinh.setText("Ngày Sinh: ");

        cmb_chucVu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_chucVu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nhân Viên Bán Hàng", "Nhân Viên Quản Lý" }));
        cmb_chucVu.setPreferredSize(new java.awt.Dimension(0, 30));
        cmb_chucVu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_chucVuActionPerformed(evt);
            }
        });

        lbl_luongCoBan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_luongCoBan.setText("Lương Cơ Bản");

        txt_luongCoBan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_luongCoBan.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_luongCoBan.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_luongCoBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_luongCoBanActionPerformed(evt);
            }
        });
        txt_luongCoBan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_luongCoBanKeyTyped(evt);
            }
        });

        cmb_trangThai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_trangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang Làm", "Nghỉ Việc" }));
        cmb_trangThai.setPreferredSize(new java.awt.Dimension(0, 30));

        lbl_ngayVaoLam.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_ngayVaoLam.setText("Ngày Vào Làm: ");

        cld_ngaySinhNV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cld_ngaySinhNV.setPreferredSize(new java.awt.Dimension(0, 30));

        cld_ngayVaoLam.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cld_ngayVaoLam.setPreferredSize(new java.awt.Dimension(0, 30));

        javax.swing.GroupLayout pnl_centerLayout = new javax.swing.GroupLayout(pnl_center);
        pnl_center.setLayout(pnl_centerLayout);
        pnl_centerLayout.setHorizontalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_centerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_centerLayout.createSequentialGroup()
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbl_tenNV)
                                .addComponent(lbl_maNV))
                            .addComponent(lbl_gioiTinh)
                            .addComponent(lbl_diaChi))
                        .addGap(12, 12, 12)
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_maNV, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_diaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txt_tenNV, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_centerLayout.createSequentialGroup()
                        .addComponent(lbl_chucVu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmb_chucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_centerLayout.createSequentialGroup()
                        .addComponent(lbl_luongCoBan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_luongCoBan, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_centerLayout.createSequentialGroup()
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_soDienThoai)
                            .addComponent(lbl_ngaySinh))
                        .addGap(12, 12, 12)
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_soDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cld_ngaySinhNV, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40)
                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_ngayVaoLam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_trangThai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmb_trangThai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cld_ngayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(343, 343, 343))
        );
        pnl_centerLayout.setVerticalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_centerLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_centerLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_centerLayout.createSequentialGroup()
                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lbl_soDienThoai)
                                    .addComponent(txt_soDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cld_ngaySinhNV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_ngaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(146, 146, 146))
                            .addGroup(pnl_centerLayout.createSequentialGroup()
                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmb_trangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_trangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cld_ngayVaoLam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lbl_ngayVaoLam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(pnl_centerLayout.createSequentialGroup()
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_maNV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_maNV))
                        .addGap(18, 18, 18)
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_tenNV)
                            .addComponent(txt_tenNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmb_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_gioiTinh)
                            .addComponent(lbl_chucVu)
                            .addComponent(cmb_chucVu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_luongCoBan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbl_luongCoBan))
                            .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_diaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lbl_diaChi)))
                        .addGap(46, 46, 46))))
        );

        pnl_top.setBackground(new java.awt.Color(255, 255, 255));

        lbl_tieuDe.setBackground(new java.awt.Color(255, 255, 255));
        lbl_tieuDe.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_tieuDe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_tieuDe.setText("QUẢN LÝ NHÂN VIÊN");

        javax.swing.GroupLayout pnl_topLayout = new javax.swing.GroupLayout(pnl_top);
        pnl_top.setLayout(pnl_topLayout);
        pnl_topLayout.setHorizontalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_tieuDe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnl_topLayout.setVerticalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_tieuDe, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnl_south, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1094, Short.MAX_VALUE)
                    .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, 1091, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scr_main, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(pnl_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(pnl_south, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addGap(24, 24, 24)
                .addComponent(scr_main, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_luongCoBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_luongCoBanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_luongCoBanActionPerformed

    private void txt_tenNVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_tenNVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tenNVActionPerformed

    private void cmb_chucVuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_chucVuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_chucVuActionPerformed

    private void cmb_locChucVuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_locChucVuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_locChucVuActionPerformed

    private void cmb_locGioiTinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_locGioiTinhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_locGioiTinhActionPerformed

    private void cmb_locTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_locTrangThaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_locTrangThaiActionPerformed

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
        try {
            if (valiData()) {
                themNhanVien();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_themActionPerformed

    private void btn_lamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lamMoiActionPerformed
        try {
            LamMoi();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_lamMoiActionPerformed

    private void btn_capNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_capNhatActionPerformed
        try {
            capNhatNhanVien();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_capNhatActionPerformed

    private void table_dsNVMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_dsNVMouseClicked
        // TODO add your handling code here:
        int r = table_dsNV.getSelectedRow(); //lay so hang

        txt_maNV.setText(table_dsNV.getValueAt(r, 0).toString());
        txt_tenNV.setText(table_dsNV.getValueAt(r, 1).toString());

        int gioiTinh;
        if (table_dsNV.getValueAt(r, 2).toString().equalsIgnoreCase("Nam")) {
            gioiTinh = 0;
        } else if (table_dsNV.getValueAt(r, 2).toString().equalsIgnoreCase("Nữ")) {
            gioiTinh = 1;
        } else {
            gioiTinh = 2;
        }
        cmb_gioiTinh.setSelectedIndex(gioiTinh);
        txt_diaChi.setText(table_dsNV.getValueAt(r, 3).toString());
        txt_soDienThoai.setText(table_dsNV.getValueAt(r, 4).toString());
        String ngaysinh = table_dsNV.getValueAt(r, 5).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        int chucVu;
        if (table_dsNV.getValueAt(r, 6).toString().equalsIgnoreCase("Nhân Viên Bán Hàng")) {
            chucVu = 0;
        } else {
            chucVu = 1;
        }
        cmb_chucVu.setSelectedIndex(chucVu);
        String giaTienChinhSua = table_dsNV.getValueAt(r, 7).toString().replace(" VNĐ", " ").replace(",", "");
        txt_luongCoBan.setText(giaTienChinhSua);

        int trangThai;
        if (table_dsNV.getValueAt(r, 8).toString().equalsIgnoreCase("Đang Làm")) {
            trangThai = 0;
        } else {
            trangThai = 1;
        }
        cmb_trangThai.setSelectedIndex(trangThai);
        String ngayVaoLam = table_dsNV.getValueAt(r, 9).toString();

        try {
            cld_ngaySinhNV.setDate(sdf.parse(ngaysinh));
        } catch (ParseException ex) {
            Logger.getLogger(panel_QuanLyKhachHang.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            cld_ngayVaoLam.setDate(sdf.parse(ngayVaoLam));
        } catch (ParseException ex) {
            Logger.getLogger(panel_QuanLyKhachHang.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_table_dsNVMouseClicked

    private void btn_locActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_locActionPerformed
        try {
            locChucVu();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_locActionPerformed

    private void txt_timKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_timKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_timKiemActionPerformed

    private void txt_timKiemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_timKiemKeyPressed
        // TODO add your handling code here:
        debounce.restart();
    }//GEN-LAST:event_txt_timKiemKeyPressed

    private void txt_soDienThoaiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soDienThoaiKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_soDienThoaiKeyTyped

    private void txt_luongCoBanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_luongCoBanKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_luongCoBanKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_capNhat;
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_loc;
    private javax.swing.JButton btn_them;
    private com.toedter.calendar.JDateChooser cld_ngaySinhNV;
    private com.toedter.calendar.JDateChooser cld_ngayVaoLam;
    private javax.swing.JComboBox<String> cmb_chucVu;
    private javax.swing.JComboBox<String> cmb_gioiTinh;
    private javax.swing.JComboBox<String> cmb_locChucVu;
    private javax.swing.JComboBox<String> cmb_locGioiTinh;
    private javax.swing.JComboBox<String> cmb_locTrangThai;
    private javax.swing.JComboBox<String> cmb_trangThai;
    private javax.swing.JLabel lbl_chucVu;
    private javax.swing.JLabel lbl_diaChi;
    private javax.swing.JLabel lbl_gioiTinh;
    private javax.swing.JLabel lbl_locChucVu;
    private javax.swing.JLabel lbl_locGioiTinh;
    private javax.swing.JLabel lbl_locTrangThai;
    private javax.swing.JLabel lbl_luongCoBan;
    private javax.swing.JLabel lbl_maNV;
    private javax.swing.JLabel lbl_ngaySinh;
    private javax.swing.JLabel lbl_ngayVaoLam;
    private javax.swing.JLabel lbl_soDienThoai;
    private javax.swing.JLabel lbl_tenNV;
    private javax.swing.JLabel lbl_tieuDe;
    private javax.swing.JLabel lbl_trangThai;
    private javax.swing.JPanel pnl_center;
    private javax.swing.JPanel pnl_south;
    private javax.swing.JPanel pnl_top;
    private javax.swing.JScrollPane scr_main;
    private javax.swing.JTable table_dsNV;
    private javax.swing.JTextField txt_diaChi;
    private javax.swing.JTextField txt_luongCoBan;
    private javax.swing.JTextField txt_maNV;
    private javax.swing.JTextField txt_soDienThoai;
    private javax.swing.JTextField txt_tenNV;
    private javax.swing.JTextField txt_timKiem;
    // End of variables declaration//GEN-END:variables

}
