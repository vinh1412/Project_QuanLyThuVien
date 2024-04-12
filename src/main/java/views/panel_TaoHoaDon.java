/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views;

import bus.ChiTietHoaDon_Bus;
import bus.HoaDon_Bus;
import bus.SanPham_Bus;
import connectDB.ConnectDB;
import dao.KhachHang_Dao;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.SanPham;
import utils.GenerateID;
import utils.NotifyToast;
import utils.getCommonIcons;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author ngoct
 */
public class panel_TaoHoaDon extends javax.swing.JPanel {

    /**
     * Creates new form panel_TaoHoaDon
     */
    private final SanPham_Bus sanPham_Bus;
    private Timer debounce;
    private JdialogHangChoHD hangChoView;
    private List<SanPham> dsSanPham;
    private final ArrayList<SanPham> dsSanPhamTimKiem;
    private ArrayList<SanPham> gioHang;
    private final DefaultTableModel modelSP;
    private final DefaultTableModel modelGioHang;
    private double tongTien = 0;
    private final KhachHang_Dao khachHang_Dao;
    private KhachHang kh;
    private double giamGia = 0;
    private final ArrayList<Object[]> dsHDCho;
    private final HoaDon_Bus hoaDon_Bus;
    private final ChiTietHoaDon_Bus cthd_Bus;

    public panel_TaoHoaDon() {
        sanPham_Bus = new SanPham_Bus();
        khachHang_Dao = new KhachHang_Dao();
        initComponents();
        customInitComponents();
        dsHDCho = new ArrayList<>();
        gioHang = new ArrayList<>();
        getAllSP();
        modelSP = (DefaultTableModel) tbl_dsSP.getModel();
        modelGioHang = (DefaultTableModel) tbl_gioHang.getModel();
        dsSanPhamTimKiem = new ArrayList<>();
        hoaDon_Bus = new HoaDon_Bus();
        cthd_Bus = new ChiTietHoaDon_Bus();
    }

    public final void getAllSP() {
        dsSanPham = sanPham_Bus.getAllSanPham();
    }

    private void timKiemSanPham() {
        dsSanPhamTimKiem.clear();
        if (!txt_timKiemSP.getText().trim().equals("")) {
            for (SanPham s : dsSanPham) {
                if (s.getMaSanPham().toLowerCase().contains(txt_timKiemSP.getText().trim().toLowerCase())
                        || s.getTenSanPham().toLowerCase().contains(txt_timKiemSP.getText().trim().toLowerCase())) {
                    dsSanPhamTimKiem.add(s);
                }
            }
        }
        renderDataToDSSP();
    }

    public void setKhachHang() {
        txt_timKiemKH.setText(Application.getSTDKH());
    }

    private void xuatHoaDon(String maHD) throws IOException {
        Hashtable map = new Hashtable();
        JasperReport report;
        try {
            report = JasperCompileManager.compileReport("src/Report/hoaDon.jrxml");
            map.put("maHD", maHD);
            java.sql.Connection conn = ConnectDB.getConnection();
            JasperPrint p = JasperFillManager.fillReport(report, map, conn);
            JasperExportManager.exportReportToHtmlFile(p, "src/ReportImg/" + maHD + ".html");
            File file = new File("src/ReportImg/" + maHD + ".html");
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();

                // Check if the file exists
                if (file.exists()) {
                    desktop.open(file);
                } else {
                    NotifyToast.showErrorToast("Không tìm thấy file");
                }
            } else {
                NotifyToast.showErrorToast("Không hỗ trợ");
            }
        } catch (JRException ex) {
            Logger.getLogger(panel_TaoHoaDon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void themHDCho() {
        String hoTen = txt_tenKH.getText();
        String SDT = txt_timKiemKH.getText();
        if (txt_maKH.getText().equals("")) {
            JPanel panel = new JPanel();
            JTextField txt_HoTenHangCho = new JTextField(10);
            JTextField txt_SDTHangCho = new JTextField(10);

            panel.add(new JLabel("Họ và Tên:"));
            panel.add(txt_HoTenHangCho);
            panel.add(new JLabel("Số Điện Thoại:"));
            panel.add(txt_SDTHangCho);

            // Hiển thị JOptionPane với panel nhập liệu
            int result = JOptionPane.showConfirmDialog(null, panel, "Nhập Thông Tin", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (txt_HoTenHangCho.equals("")) {
                    NotifyToast.showErrorToast("Vui lòng nhập họ tên khách hàng");
                    return;
                }
                if (!txt_SDTHangCho.getText().matches("(^(03)[2-9]\\d{7})|(^(07)[06-9]\\d{7})|(^(08)[1-5]\\d{7})|(^(056)\\d{7})|(^(058)\\d{7})|(^(059)\\d{7})|(^(09)[0-46-9]\\d{7})")) {
                    NotifyToast.showErrorToast("Số điện thoại không hợp lệ");
                    return;
                }

                hoTen = txt_HoTenHangCho.getText().trim();
                SDT = txt_SDTHangCho.getText().trim();
            }
        }

        ArrayList<SanPham> dsSPCho = new ArrayList<>();
        for (SanPham i : gioHang) {
            dsSPCho.add(i);
        }
        dsHDCho.add(new Object[]{hoTen, SDT, dsSPCho});
        dsSanPhamTimKiem.clear();
        txt_timKiemSP.setText("");
        gioHang.clear();
        renderDataToCart();
        renderDataToDSSP();
        lamMoiKH();
    }

    private boolean timKiemKhachHang() {
        String soDT = txt_timKiemKH.getText().trim();
        if (soDT.equals("")) {
            txt_timKiemKH.requestFocus();
            NotifyToast.showErrorToast("Vui lòng nhập số điện thoại cần tìm");
            return false;
        }
        if (!soDT.matches("(^(03)[2-9]\\d{7})|(^(07)[06-9]\\d{7})|(^(08)[1-5]\\d{7})|(^(056)\\d{7})|(^(058)\\d{7})|(^(059)\\d{7})|(^(09)[0-46-9]\\d{7})")) {
            txt_timKiemKH.requestFocus();
            NotifyToast.showErrorToast("Số điện thoại không hợp lệ");
            return false;
        }
        kh = khachHang_Dao.timKiemKhachHangTheoSDT(soDT);
        if (kh == null) {
            NotifyToast.showErrorToast("Khách hàng không tồn tại");
            return false;
        }
        txt_tenKH.setText(kh.getTenKhachHang());
        txt_maKH.setText(kh.getMaKhachHang());
        txt_diemDoiThuong.setText(Integer.toString(kh.getDiemDoiThuong()));
        return true;
    }

    private void renderDataToDSSP() {
        modelSP.setRowCount(0);
        for (SanPham i : dsSanPhamTimKiem) {
            modelSP.addRow(new Object[]{i.getMaSanPham(), i.getTenSanPham(), i.getSoLuongTon(), i.getGiaMua() * (1 + i.getVat())});
        }
    }

    private String formatVND(double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        long roundedNumber = (long) amount;
        String formattedAmount = currencyFormatter.format(roundedNumber);
        return formattedAmount;
    }

    private void renderDataToCart() {
        tongTien = 0;
        modelGioHang.setRowCount(0);
        for (SanPham i : gioHang) {
            double giaBan = i.getGiaMua() * (1 + i.getVat());
            tongTien += giaBan * i.getSoLuongTon();
            modelGioHang.addRow(new Object[]{i.getMaSanPham(), i.getTenSanPham(), i.getSoLuongTon(), giaBan, giaBan * i.getSoLuongTon()});
        }
        txt_thanhToan.setText(formatVND(tongTien - giamGia));
        txt_tongTien.setText(formatVND(tongTien));
    }

    private boolean setQuantitySP(String maSP, int soLuongMua) {
        for (SanPham i : dsSanPham) {
            if (i.getMaSanPham().equals(maSP)) {
                if (i.getSoLuongTon() < soLuongMua) {
                    return false;
                } else {
                    i.setSoLuongTon(i.getSoLuongTon() - soLuongMua);
                    break;
                }
            }
        }
        return true;
    }

    private void lamMoiKH() {
        txt_timKiemKH.setText("");
        txt_giamGia.setText("0%");
        txt_maKH.setText("");
        txt_tenKH.setText("");
        txt_diemDoiThuong.setText("0");
    }

    private boolean validDataTaoHD() {
        if (gioHang.isEmpty()) {
            NotifyToast.showErrorToast("Giỏ hàng đang trống");
            return false;
        }
        return true;
    }

    private boolean taoHD() throws SQLException, JRException, IOException {
        int indexHD = hoaDon_Bus.getThuTuHoaDon();
        NhanVien nv = Application.getTK().getNhanVien();
        String maHD = GenerateID.generateMaHoaDon(indexHD);
        Date ngayLap = new Date();
        String tenKh=txt_tenKH.getText();
        KhachHang kh;
        if(tenKh.isEmpty()){
             kh= null;
        }else{
             kh=khachHang_Dao.getKhachHangByTen(tenKh);
            
        }
        HoaDon hd = new HoaDon(maHD, ngayLap, kh, nv, giamGia);
        if (!hoaDon_Bus.themHD(hd)) {
            NotifyToast.showErrorToast("Có lỗi xảy ra");
            return false;
        }
        boolean isErr = false;
        for (SanPham i : gioHang) {
            ChiTietHoaDon cthd = new ChiTietHoaDon(i.getSoLuongTon(), hd, i, i.getGiaMua() * (1 + i.getVat()));
            SanPham spTemp = null;
            for (SanPham j : dsSanPham) {
                if (j.getMaSanPham().equals(i.getMaSanPham())) {
                    spTemp = j;
                    break;
                }
            }
            if (!cthd_Bus.themCTHD(cthd)) {
                NotifyToast.showErrorToast("Có lỗi xảy ra");
                isErr = true;
                break;
            } else {
                sanPham_Bus.updateSanPham(spTemp);

            }
        }
        if (!isErr) {
            if (kh != null) {
                kh.setDiemDoiThuong((int) (Integer.parseInt(txt_diemDoiThuong.getText()) + tongTien * 0.01));
                khachHang_Dao.capNhatKH(kh);
            }
            xuatHoaDon(hd.getMaHoaDon());
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_sanPhamInfo = new javax.swing.JPanel();
        txt_timKiemSP = new javax.swing.JTextField();
        btn_themSP = new javax.swing.JButton();
        btn_lamMoiSP = new javax.swing.JButton();
        scr_dsSP = new javax.swing.JScrollPane();
        tbl_dsSP = new javax.swing.JTable();
        scr_gioHang = new javax.swing.JScrollPane();
        tbl_gioHang = new javax.swing.JTable();
        pnl_khachHang = new javax.swing.JPanel();
        txt_timKiemKH = new javax.swing.JTextField();
        lbl_tenKH = new javax.swing.JLabel();
        lbl_maKH = new javax.swing.JLabel();
        lbl_loaiKH = new javax.swing.JLabel();
        btn_themKH = new javax.swing.JButton();
        btn_timKiemKH = new javax.swing.JButton();
        txt_maKH = new javax.swing.JTextField();
        txt_tenKH = new javax.swing.JTextField();
        txt_diemDoiThuong = new javax.swing.JTextField();
        btn_lamMoiKH = new javax.swing.JButton();
        btn_apDung = new javax.swing.JButton();
        pnl_thanhToan = new javax.swing.JPanel();
        lbl_tongTien = new javax.swing.JLabel();
        lbl_giamGia = new javax.swing.JLabel();
        lbl_tienThanhToan = new javax.swing.JLabel();
        lbl_tienKhachDua = new javax.swing.JLabel();
        txt_tienKhachDua = new javax.swing.JTextField();
        lbl_tienTraLai = new javax.swing.JLabel();
        txt_tienTraLai = new javax.swing.JTextField();
        txt_tongTien = new javax.swing.JTextField();
        txt_giamGia = new javax.swing.JTextField();
        txt_thanhToan = new javax.swing.JTextField();
        btn_hoanTac = new javax.swing.JButton();
        pnl_chucNang = new javax.swing.JPanel();
        btn_taoHD = new javax.swing.JButton();
        btn_hangCho = new javax.swing.JButton();
        btn_xemHangCho = new javax.swing.JButton();
        btn_dsHoaDon = new javax.swing.JButton();
        btn_huyHD = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1096, 768));

        txt_timKiemSP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_timKiemSP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_timKiemSPKeyPressed(evt);
            }
        });

        btn_themSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themSPActionPerformed(evt);
            }
        });

        btn_lamMoiSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lamMoiSPActionPerformed(evt);
            }
        });

        scr_dsSP.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách sản phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        tbl_dsSP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbl_dsSP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Giá bán"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_dsSP.setRowHeight(32);
        scr_dsSP.setViewportView(tbl_dsSP);

        scr_gioHang.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Giỏ hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        tbl_gioHang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbl_gioHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Đơn giá", "Tổng tiền"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_gioHang.setRowHeight(32);
        tbl_gioHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_gioHangMouseClicked(evt);
            }
        });
        scr_gioHang.setViewportView(tbl_gioHang);

        javax.swing.GroupLayout pnl_sanPhamInfoLayout = new javax.swing.GroupLayout(pnl_sanPhamInfo);
        pnl_sanPhamInfo.setLayout(pnl_sanPhamInfoLayout);
        pnl_sanPhamInfoLayout.setHorizontalGroup(
            pnl_sanPhamInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_sanPhamInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txt_timKiemSP, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_themSP, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_lamMoiSP, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(pnl_sanPhamInfoLayout.createSequentialGroup()
                .addGroup(pnl_sanPhamInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scr_dsSP, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scr_gioHang, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnl_sanPhamInfoLayout.setVerticalGroup(
            pnl_sanPhamInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_sanPhamInfoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_sanPhamInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(txt_timKiemSP, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_themSP, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btn_lamMoiSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scr_dsSP, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scr_gioHang, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118))
        );

        pnl_khachHang.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin khách hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        txt_timKiemKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lbl_tenKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_tenKH.setText("Tên khách hàng:");

        lbl_maKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_maKH.setText("Mã khách hàng: ");

        lbl_loaiKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_loaiKH.setText("Điểm đổi thưởng");

        btn_themKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themKHActionPerformed(evt);
            }
        });

        btn_timKiemKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_timKiemKH.setText("Tìm kiếm");
        btn_timKiemKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_timKiemKHActionPerformed(evt);
            }
        });

        txt_maKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_maKH.setEnabled(false);

        txt_tenKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tenKH.setEnabled(false);

        txt_diemDoiThuong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_diemDoiThuong.setText("0");
        txt_diemDoiThuong.setEnabled(false);

        btn_lamMoiKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lamMoiKHActionPerformed(evt);
            }
        });

        btn_apDung.setText("Áp dụng");
        btn_apDung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_apDungActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_khachHangLayout = new javax.swing.GroupLayout(pnl_khachHang);
        pnl_khachHang.setLayout(pnl_khachHangLayout);
        pnl_khachHangLayout.setHorizontalGroup(
            pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_khachHangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnl_khachHangLayout.createSequentialGroup()
                        .addComponent(txt_timKiemKH, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_timKiemKH, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_themKH, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(pnl_khachHangLayout.createSequentialGroup()
                        .addGroup(pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_loaiKH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_tenKH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_maKH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnl_khachHangLayout.createSequentialGroup()
                                .addComponent(txt_diemDoiThuong, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_apDung))
                            .addComponent(txt_tenKH)
                            .addComponent(txt_maKH)))
                    .addComponent(btn_lamMoiKH, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_khachHangLayout.setVerticalGroup(
            pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_khachHangLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_themKH, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_timKiemKH, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_timKiemKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_maKH)
                    .addComponent(txt_maKH, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_tenKH)
                    .addComponent(txt_tenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(pnl_khachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_loaiKH)
                    .addComponent(txt_diemDoiThuong, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_apDung, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btn_lamMoiKH, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        lbl_tongTien.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_tongTien.setText("Tổng tiền: ");

        lbl_giamGia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_giamGia.setText("Giảm giá:");

        lbl_tienThanhToan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_tienThanhToan.setText("Thanh toán:");

        lbl_tienKhachDua.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_tienKhachDua.setText("Tiền khách đưa:");

        txt_tienKhachDua.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tienKhachDua.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txt_tienKhachDuaPropertyChange(evt);
            }
        });
        txt_tienKhachDua.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_tienKhachDuaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tienKhachDuaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_tienKhachDuaKeyTyped(evt);
            }
        });

        lbl_tienTraLai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_tienTraLai.setText("Tiền trả lại:");

        txt_tienTraLai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tienTraLai.setEnabled(false);

        txt_tongTien.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tongTien.setEnabled(false);

        txt_giamGia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_giamGia.setText("0");
        txt_giamGia.setEnabled(false);

        txt_thanhToan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_thanhToan.setEnabled(false);

        btn_hoanTac.setText("Hoàn tác");
        btn_hoanTac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hoanTacActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_thanhToanLayout = new javax.swing.GroupLayout(pnl_thanhToan);
        pnl_thanhToan.setLayout(pnl_thanhToanLayout);
        pnl_thanhToanLayout.setHorizontalGroup(
            pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_thanhToanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_tienKhachDua)
                    .addComponent(lbl_tienTraLai)
                    .addComponent(lbl_tongTien)
                    .addComponent(lbl_giamGia)
                    .addComponent(lbl_tienThanhToan))
                .addGap(18, 18, 18)
                .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txt_tienKhachDua, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(txt_thanhToan, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_tongTien, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_giamGia, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_tienTraLai))
                .addGap(18, 18, 18)
                .addComponent(btn_hoanTac)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_thanhToanLayout.setVerticalGroup(
            pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_thanhToanLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_tongTien, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(lbl_tongTien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_giamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_hoanTac, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_giamGia, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_thanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(lbl_tienThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_tienKhachDua, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(lbl_tienKhachDua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnl_thanhToanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_tienTraLai, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_tienTraLai, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );

        btn_taoHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_taoHDActionPerformed(evt);
            }
        });

        btn_hangCho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hangChoActionPerformed(evt);
            }
        });

        btn_xemHangCho.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_xemHangCho.setText("Xem hàng chờ");
        btn_xemHangCho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_xemHangChoActionPerformed(evt);
            }
        });

        btn_dsHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_dsHoaDon.setText("Danh sách hóa đơn");
        btn_dsHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_dsHoaDonActionPerformed(evt);
            }
        });

        btn_huyHD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_huyHD.setText("Hủy hóa đơn");
        btn_huyHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_huyHDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_chucNangLayout = new javax.swing.GroupLayout(pnl_chucNang);
        pnl_chucNang.setLayout(pnl_chucNangLayout);
        pnl_chucNangLayout.setHorizontalGroup(
            pnl_chucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_chucNangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_chucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_dsHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnl_chucNangLayout.createSequentialGroup()
                        .addComponent(btn_taoHD, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_hangCho, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnl_chucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_huyHD, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                    .addComponent(btn_xemHangCho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_chucNangLayout.setVerticalGroup(
            pnl_chucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_chucNangLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_chucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_dsHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(btn_huyHD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30)
                .addGroup(pnl_chucNangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btn_xemHangCho, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(btn_hangCho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_taoHD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_sanPhamInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_thanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_khachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pnl_chucNang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(pnl_sanPhamInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnl_khachHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(pnl_thanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnl_chucNang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_lamMoiSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lamMoiSPActionPerformed
        // TODO add your handling code here:
        txt_timKiemSP.setText("");
    }//GEN-LAST:event_btn_lamMoiSPActionPerformed

    private void btn_themSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themSPActionPerformed
        // TODO add your handling code here:
        List<SanPham> listSP = sanPham_Bus.timKiemSanPham(txt_timKiemSP.getText().trim());
        

        int selectedRow = tbl_dsSP.getSelectedRow();
      
        if (selectedRow == -1 ) {
            NotifyToast.showErrorToast("Chọn sản phẩm cần thêm");
            return;
        }
        String soLuong = JOptionPane.showInputDialog("Nhập số lượng sản phẩm:");
        if (soLuong != null) {
            try {
                int quantity = Integer.parseInt(soLuong);
                if (quantity == 0 || quantity < 0) {
                    return;
                }
                
                Object value = tbl_dsSP.getValueAt(selectedRow, 0);
                String firstCellValue = String.valueOf(value);
                SanPham i = sanPham_Bus.getSanPhamTheoMa(firstCellValue);
                 
//                SanPham i = listSP.get(selectedRow);

                i.setSoLuongTon(quantity);
                boolean setSuccess = setQuantitySP(i.getMaSanPham(), quantity);
                if (setSuccess) {
                    boolean tonTai = false;
                    for (SanPham j : gioHang) {
                        if (j.getMaSanPham().equals(i.getMaSanPham())) {
                            j.setSoLuongTon(j.getSoLuongTon() + i.getSoLuongTon());
                            tonTai = true;
                            break;
                        }
                    }
                    if (!tonTai) {
                        gioHang.add(i);
                        renderDataToCart();
                    } else {
                        renderDataToCart();
                    }
                    timKiemSanPham();
                } else {
                    NotifyToast.showErrorToast("Không đủ số lượng sản phẩm");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập một số nguyên dương.");
            }
        }
    }//GEN-LAST:event_btn_themSPActionPerformed

    private void txt_timKiemSPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_timKiemSPKeyPressed
        // TODO add your handling code here:
        debounce.restart();
    }//GEN-LAST:event_txt_timKiemSPKeyPressed

    private void tbl_gioHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_gioHangMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int selectedRow = tbl_gioHang.getSelectedRow();
            if (selectedRow != -1) {
                String soLuong = JOptionPane.showInputDialog("Nhập số lượng sản phẩm mới:");
                String maSP = (String) tbl_gioHang.getValueAt(selectedRow, 0);
                 int quantity = Integer.parseInt(soLuong);
                if (soLuong.equals("0")) {
                    for (SanPham g : gioHang) {
                        if (g.getMaSanPham().equals(maSP)) {
                            gioHang.remove(g);
                            break;
                        }
                    }
                }
                if(quantity > 0){
                    
                    try {
//                    int quantity = Integer.parseInt(soLuong);
                  
                         int currentQty = (int) tbl_gioHang.getValueAt(selectedRow, 2);
                    boolean setSuccess = setQuantitySP(maSP, quantity - currentQty);
                    if (setSuccess) {
                        for (SanPham s : gioHang) {
                            if (s.getMaSanPham().equals(maSP)) {
                                s.setSoLuongTon(quantity);
                                break;
                            }
                        }
                        timKiemSanPham();
                        renderDataToCart();
                    } else {
                        NotifyToast.showErrorToast("Không đủ số lượng sản phẩm");
                    }
                    
                   
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Vui lòng nhập một số nguyên dương.");
                     }
                    
                }
                
                    
                
              
            }
        }

    }//GEN-LAST:event_tbl_gioHangMouseClicked

    private void btn_timKiemKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_timKiemKHActionPerformed
        // TODO add your handling code here:
        timKiemKhachHang();
    }//GEN-LAST:event_btn_timKiemKHActionPerformed

    private void btn_themKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themKHActionPerformed
        // TODO add your handling code here:
        Application.showForm(new panel_QuanLyKhachHang(false));
    }//GEN-LAST:event_btn_themKHActionPerformed

    private void btn_hangChoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hangChoActionPerformed
        // TODO add your handling code here:
        if (gioHang.isEmpty()) {
            NotifyToast.showErrorToast("Giỏ hàng đang trống");
        } else
            themHDCho();
    }//GEN-LAST:event_btn_hangChoActionPerformed

    private void btn_xemHangChoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_xemHangChoActionPerformed
        // TODO add your handling code here:

        hangChoView = new JdialogHangChoHD(null, true, dsHDCho);
        hangChoView.setVisible(true);
        hangChoView.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                Object[] user = hangChoView.getUser();
                if (user != null) {
                    String SDT = (String) user[1];
                    kh = khachHang_Dao.timKiemKhachHangTheoSDT(SDT);
                    if (kh != null) {
                        txt_tenKH.setText(kh.getTenKhachHang());
                        txt_maKH.setText(kh.getMaKhachHang());
                        txt_diemDoiThuong.setText(Integer.toString(kh.getDiemDoiThuong()));
                    }
                    for (Object[] o : dsHDCho) {
                        if (o[1].equals(SDT)) {
                            gioHang = (ArrayList<SanPham>) o[2];
                            dsHDCho.remove(o);
                            break;
                        }
                    }
                    renderDataToCart();
                }
            }
        });
    }//GEN-LAST:event_btn_xemHangChoActionPerformed

    private void btn_taoHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_taoHDActionPerformed
        // TODO add your handling code here:
        if (validDataTaoHD()) {
            try {
                if (taoHD()) {
                    NotifyToast.showSuccessToast("Tạo hóa đơn thành công");
                    lamMoiKH();
                    dsSanPhamTimKiem.clear();
                    gioHang.clear();
                    txt_timKiemSP.setText("");
                    tongTien = 0;
                    giamGia = 0;
                    renderDataToCart();
                    renderDataToDSSP();
                }
            } catch (SQLException ex) {
                Logger.getLogger(panel_TaoHoaDon.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JRException ex) {
                Logger.getLogger(panel_TaoHoaDon.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(panel_TaoHoaDon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btn_taoHDActionPerformed

    private void btn_lamMoiKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lamMoiKHActionPerformed
        // TODO add your handling code here:
        lamMoiKH();
    }//GEN-LAST:event_btn_lamMoiKHActionPerformed

    private void btn_dsHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dsHoaDonActionPerformed
        try {
            // TODO add your handling code here:
            Application.showForm(new panel_DanhSachHoaDon());
        } catch (SQLException ex) {
            Logger.getLogger(panel_TaoHoaDon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_dsHoaDonActionPerformed

    private void btn_apDungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_apDungActionPerformed
        // TODO add your handling code here:
        giamGia = Integer.parseInt(txt_diemDoiThuong.getText());
        txt_diemDoiThuong.setText("0");
        if (kh != null) {
            txt_giamGia.setText(Integer.toString(kh.getDiemDoiThuong()));
            giamGia = kh.getDiemDoiThuong();
        } else {
            txt_giamGia.setText("0");
            giamGia = 0;
        }
        txt_thanhToan.setText(formatVND(tongTien - giamGia));
        double tienDua = 0;
        if (!txt_tienKhachDua.getText().equals(0) && !txt_tienKhachDua.getText().equals("")) {
            tienDua = Double.parseDouble(txt_tienKhachDua.getText());
        }
        double thanhToan = 0;
        if (!txt_thanhToan.getText().equals("")) {
            thanhToan = tongTien - giamGia;
        }
        txt_tienTraLai.setText(formatVND(tienDua - thanhToan));
    }//GEN-LAST:event_btn_apDungActionPerformed

    private void btn_hoanTacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hoanTacActionPerformed
        // TODO add your handling code here:
        txt_thanhToan.setText(formatVND(tongTien));
        giamGia = 0;
        giamGia = Integer.parseInt(txt_diemDoiThuong.getText());
        if (kh != null) {
            txt_diemDoiThuong.setText(Integer.toString(kh.getDiemDoiThuong()));
        } else {
            txt_diemDoiThuong.setText("0");
        }
        txt_giamGia.setText("0");
        double tienDua = 0;
        if (!txt_tienKhachDua.getText().equals(0) && !txt_tienKhachDua.getText().equals("")) {
            tienDua = Double.parseDouble(txt_tienKhachDua.getText());
        }
        double thanhToan = 0;
        if (!txt_thanhToan.getText().equals("")) {
            thanhToan = tongTien - giamGia;
        }
        txt_tienTraLai.setText(formatVND(tienDua - thanhToan));
    }//GEN-LAST:event_btn_hoanTacActionPerformed

    private void txt_tienKhachDuaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tienKhachDuaKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_tienKhachDuaKeyTyped

    private void txt_tienKhachDuaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tienKhachDuaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tienKhachDuaKeyPressed

    private void txt_tienKhachDuaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tienKhachDuaKeyReleased
        // TODO add your handling code here:
       if(!txt_tienKhachDua.getText().isEmpty()){
           double tienDua = 0;
        if (!txt_tienKhachDua.getText().equals(0)) {
            tienDua = Double.parseDouble(txt_tienKhachDua.getText());
        }
        double thanhToan = 0;
        if (!txt_thanhToan.getText().equals("")) {
            thanhToan = tongTien - giamGia;
        }
        txt_tienTraLai.setText(formatVND(tienDua - thanhToan));
         
       }else{
              txt_tienTraLai.setText("");
               }
        
        
       
    }//GEN-LAST:event_txt_tienKhachDuaKeyReleased

    private void btn_huyHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_huyHDActionPerformed
        // TODO add your handling code here:
        lamMoiKH();
        dsSanPhamTimKiem.clear();
        gioHang.clear();
        txt_timKiemSP.setText("");
        tongTien = 0;
        giamGia = 0;
        txt_tienKhachDua.setText("");
        txt_tienTraLai.setText("");
        getAllSP();
        renderDataToCart();
        renderDataToDSSP();
    }//GEN-LAST:event_btn_huyHDActionPerformed

    private void txt_tienKhachDuaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txt_tienKhachDuaPropertyChange
       
    }//GEN-LAST:event_txt_tienKhachDuaPropertyChange
    private void customInitComponents() {
        txt_timKiemSP.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã sản phẩm, tên sản phẩm");
        txt_timKiemKH.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập số điện thoại khách hàng");
        btn_themSP.setIcon(getCommonIcons.addIcon());
        btn_lamMoiSP.setIcon(getCommonIcons.refreshIcon());
        btn_taoHD.setIcon(getCommonIcons.addIcon());
        btn_hangCho.setIcon(getCommonIcons.queueIcon());
        btn_themKH.setIcon(getCommonIcons.addIcon());
        btn_lamMoiKH.setIcon(getCommonIcons.refreshIcon());
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            timKiemSanPham();
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_apDung;
    private javax.swing.JButton btn_dsHoaDon;
    private javax.swing.JButton btn_hangCho;
    private javax.swing.JButton btn_hoanTac;
    private javax.swing.JButton btn_huyHD;
    private javax.swing.JButton btn_lamMoiKH;
    private javax.swing.JButton btn_lamMoiSP;
    private javax.swing.JButton btn_taoHD;
    private javax.swing.JButton btn_themKH;
    private javax.swing.JButton btn_themSP;
    private javax.swing.JButton btn_timKiemKH;
    private javax.swing.JButton btn_xemHangCho;
    private javax.swing.JLabel lbl_giamGia;
    private javax.swing.JLabel lbl_loaiKH;
    private javax.swing.JLabel lbl_maKH;
    private javax.swing.JLabel lbl_tenKH;
    private javax.swing.JLabel lbl_tienKhachDua;
    private javax.swing.JLabel lbl_tienThanhToan;
    private javax.swing.JLabel lbl_tienTraLai;
    private javax.swing.JLabel lbl_tongTien;
    private javax.swing.JPanel pnl_chucNang;
    private javax.swing.JPanel pnl_khachHang;
    private javax.swing.JPanel pnl_sanPhamInfo;
    private javax.swing.JPanel pnl_thanhToan;
    private javax.swing.JScrollPane scr_dsSP;
    private javax.swing.JScrollPane scr_gioHang;
    private javax.swing.JTable tbl_dsSP;
    private javax.swing.JTable tbl_gioHang;
    private javax.swing.JTextField txt_diemDoiThuong;
    private javax.swing.JTextField txt_giamGia;
    private javax.swing.JTextField txt_maKH;
    private javax.swing.JTextField txt_tenKH;
    private javax.swing.JTextField txt_thanhToan;
    private javax.swing.JTextField txt_tienKhachDua;
    private javax.swing.JTextField txt_tienTraLai;
    private javax.swing.JTextField txt_timKiemKH;
    private javax.swing.JTextField txt_timKiemSP;
    private javax.swing.JTextField txt_tongTien;
    // End of variables declaration//GEN-END:variables
}
