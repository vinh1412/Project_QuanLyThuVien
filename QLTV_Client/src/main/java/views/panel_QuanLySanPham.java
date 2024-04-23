package views;

import bus.DanhMuc_Bus;
import bus.NhaCungCap_Bus;
import bus.NhaXuatBan_Bus;
import bus.SanPham_Bus;
import bus.TacGia_Bus;
import bus.TheLoai_Bus;
import entity.DanhMuc;
import entity.NhaCungCap;
import entity.NhaXuatBan;
import entity.SanPham;
import entity.TacGia;
import entity.TheLoai;
import utils.*;
import com.formdev.flatlaf.FlatClientProperties;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class panel_QuanLySanPham extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private final SanPham_Bus sanPham_Bus;
    private Timer debounce;
    private final DefaultTableModel model;
//    private List<SanPham> dsSP=new ArrayList<>();

    public panel_QuanLySanPham() throws RemoteException, MalformedURLException, NotBoundException {
        initComponents();
        customInitComponents();
        sanPham_Bus = (SanPham_Bus) Naming.lookup(URL + "SanPham");
        model = (DefaultTableModel) table_dsSP.getModel();
        renderDataToView();
        renderDataToComboBox();
    }

    private void customInitComponents() {
        txt_timKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm Kiếm...");
        btn_them.setIcon(IconUtils.addIcon());
        btn_capNhat.setIcon(IconUtils.updateIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
        btn_locSP.setIcon(IconUtils.filterIcon());
        table_dsSP.setAutoCreateRowSorter(true);
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            try {
                timKiemSanPham();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    private void renderDataToComboBox() throws RemoteException, MalformedURLException, NotBoundException {
        TheLoai_Bus tl_Bus = (TheLoai_Bus) Naming.lookup(URL + "TheLoai");
        NhaCungCap_Bus ncc_Bus = (NhaCungCap_Bus) Naming.lookup(URL + "NhaCungCap");
        DanhMuc_Bus dm_Bus = (DanhMuc_Bus) Naming.lookup(URL + "DanhMuc");
        TacGia_Bus tg_Bus = (TacGia_Bus) Naming.lookup(URL + "TacGia");
        NhaXuatBan_Bus nxb_Bus = (NhaXuatBan_Bus) Naming.lookup(URL + "NhaXuatBan");

        DefaultComboBoxModel<DanhMuc> comboBoxModelDM = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<TheLoai> comboBoxModelTL = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<NhaCungCap> comboBoxModelNCC = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<TacGia> comboBoxModelTG = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<TheLoai> comboBoxModelTL1 = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<NhaCungCap> comboBoxModelNCC1 = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<TacGia> comboBoxModelTG1 = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<NhaXuatBan> comboBoxModelNXB = new DefaultComboBoxModel<>();
//        Fake first Element
        DanhMuc fakeDMuc = new DanhMuc(0, "Tất cả");
        TheLoai fakeTheLoai = new TheLoai(0, "Tất cả", fakeDMuc);
        NhaCungCap fakeNCC = new NhaCungCap("", "Tất cả", "", "");
        TacGia fakeTG = new TacGia("", "Tất cả", "", 0);

//        Add to model
        comboBoxModelDM.addElement(fakeDMuc);
        comboBoxModelTL.addElement(fakeTheLoai);
        comboBoxModelNCC.addElement(fakeNCC);
        comboBoxModelTG.addElement(fakeTG);
        for (TheLoai i : tl_Bus.getAllTheLoai()) {
            comboBoxModelTL.addElement(i);
            comboBoxModelTL1.addElement(i);
        }
        for (NhaCungCap i : ncc_Bus.getAllNCC()) {
            comboBoxModelNCC.addElement(i);
            comboBoxModelNCC1.addElement(i);
        }
        for (TacGia i : tg_Bus.getAllTacGia()) {
            comboBoxModelTG.addElement(i);
            comboBoxModelTG1.addElement(i);
        }
        for (DanhMuc i : dm_Bus.getAllDanhMuc()) {
            comboBoxModelDM.addElement(i);
        }
        for (NhaXuatBan i : nxb_Bus.getAllNXB()) {
            comboBoxModelNXB.addElement(i);
        }
        cmb_nhaCungCap.setModel(comboBoxModelNCC);
        cmb_tacGia.setModel(comboBoxModelTG);
        cmb_theLoai.setModel(comboBoxModelTL);
        cmb_danhMuc.setModel(comboBoxModelDM);

        cmb_nhaCungCapTop.setModel(comboBoxModelNCC1);
        cmb_tacGiaTop.setModel(comboBoxModelTG1);
        cmb_theLoaiTop.setModel(comboBoxModelTL1);
        cmb_nhaXuatBanTop.setModel(comboBoxModelNXB);
        TheLoai obj = (TheLoai) cmb_theLoaiTop.getSelectedItem();
//        2 case -> Sách or VPP
        if (obj != null) {
            switch (obj.getDanhMuc().getTenDanhMuc()) {
                case "Sách" -> txt_vat.setText("0.08");
                case "Văn Phòng Phẩm" -> txt_vat.setText("0.05");
                default -> txt_vat.setText("0");
            }
        }
    }

    private void renderDataToView() throws RemoteException {
//        System.out.println("Views.panel_QuanLySanPham.renderDataToView()");
        List<SanPham> dsSanPham = sanPham_Bus.getAllSanPham();
        model.setRowCount(0);
        for (SanPham i : dsSanPham) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedAmount = decimalFormat.format(i.getGiaMua());
            formattedAmount += " VNĐ";
            model.addRow(new Object[]{i.getMaSanPham(), i.getTenSanPham(), i.getTheLoai(), i.getNhaCungCap(), i.getSoLuongTon(), formattedAmount, i.getSoTrang()});
        }
    }

    private void timKiemSanPham() throws RemoteException {
        List<SanPham> dsSP = sanPham_Bus.timKiemSanPham(txt_timKiem.getText().trim());
        model.setRowCount(0);
        for (SanPham i : dsSP) {
            model.addRow(new Object[]{i.getMaSanPham(), i.getTenSanPham(), i.getTheLoai(), i.getNhaCungCap(), i.getSoLuongTon(), i.getGiaMua(), i.getSoTrang()});
        }
    }

    private void locSanPham() throws RemoteException {
        List<SanPham> dsSP = sanPham_Bus.locSanPham((NhaCungCap) cmb_nhaCungCap.getSelectedItem(), (TacGia) cmb_tacGia.getSelectedItem(),
                (DanhMuc) cmb_danhMuc.getSelectedItem(), (TheLoai) cmb_theLoai.getSelectedItem());
        model.setRowCount(0);
        for (SanPham i : dsSP) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedAmount = decimalFormat.format(i.getGiaMua());

            formattedAmount += " VNĐ";
            model.addRow(new Object[]{i.getMaSanPham(), i.getTenSanPham(), i.getTheLoai(), i.getNhaCungCap(), i.getSoLuongTon(), formattedAmount, i.getSoTrang()});
        }
    }

    private boolean validData() {
        if (txt_tenSP.getText().trim().equals("")) {
            txt_tenSP.requestFocus();
            NotifyToast.showErrorToast("Tên sản phẩm không được để trống");
            return false;
        }
        if (txt_soLuong.getText().trim().equals("")) {
            txt_soLuong.requestFocus();
            NotifyToast.showErrorToast("Số lượng không được để trống");
            return false;
        }
        if (Integer.parseInt(txt_soLuong.getText().trim()) == 0) {
            txt_soLuong.requestFocus();
            NotifyToast.showErrorToast("Số lượng phải lớn hơn 0");
            return false;
        }
        if (txt_giaMua.getText().trim().equals("")) {
            txt_giaMua.requestFocus();
            NotifyToast.showErrorToast("Gía mua không được để trống");
            return false;
        }
        if (Integer.parseInt(txt_giaMua.getText().trim()) == 0) {
            txt_giaMua.requestFocus();
            NotifyToast.showErrorToast("Giá mua phải lớn hơn 0");
            return false;
        }
        if (cmb_theLoaiTop.getSelectedItem().toString().contains("Sách") && txt_soTrang.getText().trim().equals("")) {
            txt_soTrang.requestFocus();
            NotifyToast.showErrorToast("Số trang không được để trống");
            return false;
        }
        if (cmb_theLoaiTop.getSelectedItem().toString().contains("Sách") && Integer.parseInt(txt_soTrang.getText().trim()) == 0) {
            txt_soTrang.requestFocus();
            NotifyToast.showErrorToast("Số trang phải lớn hơn 0");
            return false;
        }
        if (txt_moTa.getText().trim().equals("")) {
            txt_moTa.requestFocus();
            NotifyToast.showErrorToast("Mô tả sản phẩm không được để trống");
            return false;
        }
        return true;
    }

    private void themSanPham() throws RemoteException {
        if (!txt_maSP.getText().trim().equals("")) {
            NotifyToast.showErrorToast("Sản phẩm đã tồn tại");
//            return false;
            return;
        }
        SanPham sp;
        int soThuTuSP = sanPham_Bus.getThuTuSanPham();
        String maSP = GenerateID.generateMaSP(soThuTuSP);
        String tenSP = txt_tenSP.getText().trim();
        int soLuong = Integer.parseInt(txt_soLuong.getText().trim());
        double giaMua = Double.parseDouble(txt_giaMua.getText().trim());
        int soTrang = Integer.parseInt(txt_soTrang.getText().trim());
        String moTa = txt_moTa.getText().trim();
        double vat = Double.parseDouble(txt_vat.getText().trim());
        TheLoai theLoai = (TheLoai) cmb_theLoaiTop.getSelectedItem();
        NhaCungCap ncc = (NhaCungCap) cmb_nhaCungCapTop.getSelectedItem();
        TacGia tacGia = (TacGia) cmb_tacGiaTop.getSelectedItem();
        NhaXuatBan nxb = (NhaXuatBan) cmb_nhaXuatBanTop.getSelectedItem();
        String anhSP = lbl_HinhAnhView.getIcon().toString();
        String regex = "javax\\.swing\\.ImageIcon@([0-9a-f]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(anhSP);
        if (matcher.matches()) {
            NotifyToast.showErrorToast("Không được để trống ảnh sản phẩm");
//            return false;
        } else {
            anhSP = anhSP.substring(anhSP.indexOf("src"));
            sp = new SanPham(maSP, tenSP, giaMua, anhSP, ncc, tacGia, soTrang, theLoai, moTa, nxb, soLuong, vat);
            boolean check = sanPham_Bus.themSanPham(sp);
            if (check) {
                NotifyToast.showSuccessToast("Thêm sản phẩm thành công");
                lamMoi();
            } else {
                NotifyToast.showErrorToast("Thêm sản phẩm thất bại");
            }
        }
    }

    private boolean updateSanPham() throws SQLException, RemoteException {
        String maSP = txt_maSP.getText().trim();
        String tenSP = txt_tenSP.getText().trim();
        int soLuong = Integer.parseInt(txt_soLuong.getText().trim());
        double giaMua = Double.parseDouble(txt_giaMua.getText().trim());
        int soTrang = Integer.parseInt(txt_soTrang.getText().trim());
        String moTa = txt_moTa.getText().trim();
        double vat = Double.parseDouble(txt_vat.getText().trim());
        NhaCungCap ncc = (NhaCungCap) cmb_nhaCungCapTop.getSelectedItem();
        TacGia tacGia = (TacGia) cmb_tacGiaTop.getSelectedItem();
        TheLoai theLoai = (TheLoai) cmb_theLoaiTop.getSelectedItem();
        NhaXuatBan nxb = (NhaXuatBan) cmb_nhaXuatBanTop.getSelectedItem();
        String anhSP = lbl_HinhAnhView.getIcon().toString();
        anhSP = anhSP.substring(anhSP.indexOf("src"));
        SanPham sp = new SanPham(maSP, tenSP, giaMua, anhSP, ncc, tacGia, soTrang, theLoai, moTa, nxb, soLuong, vat);
        return sanPham_Bus.updateSanPham(sp);
    }

    private SanPham getSPByMa(String maSP) throws RemoteException {
        List<SanPham> dsSP = sanPham_Bus.getAllSanPham();
        for (SanPham i : dsSP) {
            if (i.getMaSanPham().equals(maSP)) {
                return i;
            }
        }
        return null;
    }

    private void lamMoi() {
        txt_giaMua.setText("");
        txt_maSP.setText("");
        txt_moTa.setText("");
        txt_soLuong.setText("");
        txt_soTrang.setText("");
        txt_tenSP.setText("");
        txt_vat.setText("0.08");
        cmb_theLoaiTop.setSelectedIndex(0);
        cmb_nhaCungCapTop.setSelectedIndex(0);
        cmb_nhaXuatBanTop.setSelectedIndex(0);
        cmb_tacGiaTop.setSelectedIndex(0);

        cmb_danhMuc.setSelectedIndex(0);
        cmb_nhaCungCap.setSelectedIndex(0);
        cmb_tacGia.setSelectedIndex(0);
        cmb_theLoai.setSelectedIndex(0);
        try {
            model.setRowCount(0);
            renderDataToView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scr_main = new javax.swing.JScrollPane();
        table_dsSP = new javax.swing.JTable();
        pnl_south = new javax.swing.JPanel();
        txt_timKiem = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_capNhat = new javax.swing.JButton();
        btn_locSP = new javax.swing.JButton();
        btn_lamMoi = new javax.swing.JButton();
        lbl_nhaCungCap = new javax.swing.JLabel();
        cmb_nhaCungCap = new javax.swing.JComboBox<>();
        lbl_tacGia = new javax.swing.JLabel();
        cmb_tacGia = new javax.swing.JComboBox<>();
        lbl_danhMuc = new javax.swing.JLabel();
        cmb_danhMuc = new javax.swing.JComboBox<>();
        lbl_theLoai = new javax.swing.JLabel();
        cmb_theLoai = new javax.swing.JComboBox<>();
        btn_danhMuc = new javax.swing.JButton();
        pnl_center = new javax.swing.JPanel();
        lbl_maSP = new javax.swing.JLabel();
        txt_maSP = new javax.swing.JTextField();
        lbl_theLoaiTop = new javax.swing.JLabel();
        lbl_soLuong = new javax.swing.JLabel();
        lbl_tenSP = new javax.swing.JLabel();
        txt_tenSP = new javax.swing.JTextField();
        lbl_giaMua = new javax.swing.JLabel();
        txt_giaMua = new javax.swing.JTextField();
        lbl_nhaXuatBan = new javax.swing.JLabel();
        lbl_soTrang = new javax.swing.JLabel();
        lbl_NhaCungCapTop = new javax.swing.JLabel();
        cmb_tacGiaTop = new javax.swing.JComboBox<>();
        lbl_moTa = new javax.swing.JLabel();
        txt_moTa = new javax.swing.JTextField();
        lbl_tacGiaTop = new javax.swing.JLabel();
        txt_vat = new javax.swing.JTextField();
        lbl_vat = new javax.swing.JLabel();
        pnl_hinhAnh = new javax.swing.JPanel();
        lbl_HinhAnh = new javax.swing.JLabel();
        cmb_theLoaiTop = new javax.swing.JComboBox<>();
        cmb_nhaCungCapTop = new javax.swing.JComboBox<>();
        txt_soTrang = new javax.swing.JTextField();
        cmb_nhaXuatBanTop = new javax.swing.JComboBox<>();
        txt_soLuong = new javax.swing.JTextField();
        lbl_HinhAnhView = new javax.swing.JLabel();

        setBackground(new java.awt.Color(240, 240, 240));

        scr_main.setBackground(new java.awt.Color(255, 255, 255));
        scr_main.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách sản phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        scr_main.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        table_dsSP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_dsSP.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Mã sản phẩm", "Tên sản phẩm", "Thể loại", "Nhà cung cấp", "Số lượng", "Giá mua", "Số trang"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        table_dsSP.setAutoscrolls(false);
        table_dsSP.setRowHeight(32);
        table_dsSP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    table_dsSPMouseClicked(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scr_main.setViewportView(table_dsSP);

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

        btn_locSP.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_locSP.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_locSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btn_locSPActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btn_lamMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_lamMoi.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_lamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lamMoiActionPerformed(evt);
            }
        });

        lbl_nhaCungCap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_nhaCungCap.setText("Nhà cung cấp:");

        cmb_nhaCungCap.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_nhaCungCap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_nhaCungCapActionPerformed(evt);
            }
        });

        lbl_tacGia.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tacGia.setText("Tác giả:");

        cmb_tacGia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_tacGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_tacGiaActionPerformed(evt);
            }
        });

        lbl_danhMuc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_danhMuc.setText("Danh mục:");

        cmb_danhMuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lbl_theLoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_theLoai.setText("Thể loại:");

        cmb_theLoai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btn_danhMuc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_danhMuc.setText("Xem thể loại");
        btn_danhMuc.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_danhMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btn_danhMucActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        javax.swing.GroupLayout pnl_southLayout = new javax.swing.GroupLayout(pnl_south);
        pnl_south.setLayout(pnl_southLayout);
        pnl_southLayout.setHorizontalGroup(
                pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_southLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lbl_nhaCungCap)
                                        .addComponent(lbl_danhMuc))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(cmb_nhaCungCap, 0, 213, Short.MAX_VALUE)
                                        .addComponent(cmb_danhMuc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_southLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnl_southLayout.createSequentialGroup()
                                                .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(270, 270, 270))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_southLayout.createSequentialGroup()
                                                .addComponent(lbl_tacGia)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmb_tacGia, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(58, 58, 58)))
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnl_southLayout.createSequentialGroup()
                                                .addComponent(btn_danhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnl_southLayout.createSequentialGroup()
                                                .addComponent(lbl_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(cmb_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btn_locSP, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18))
        );
        pnl_southLayout.setVerticalGroup(
                pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_southLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(pnl_southLayout.createSequentialGroup()
                                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(btn_danhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(23, 23, 23)
                                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lbl_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(cmb_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addComponent(cmb_tacGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(cmb_nhaCungCap)
                                                        .addComponent(lbl_nhaCungCap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(lbl_tacGia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(pnl_southLayout.createSequentialGroup()
                                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btn_locSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lbl_danhMuc)
                                        .addComponent(cmb_danhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 20, Short.MAX_VALUE))
        );

        pnl_center.setBackground(new java.awt.Color(255, 255, 255));
        pnl_center.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Sản Phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pnl_center.setMaximumSize(new java.awt.Dimension(52767, 32767));
        pnl_center.setPreferredSize(new java.awt.Dimension(1520, 100));

        lbl_maSP.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_maSP.setText("Mã sản phẩm:");

        txt_maSP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_maSP.setEnabled(false);
        txt_maSP.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_maSP.setPreferredSize(new java.awt.Dimension(0, 30));

        lbl_theLoaiTop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_theLoaiTop.setText("Thể loại:");

        lbl_soLuong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_soLuong.setText("Số lượng:");

        lbl_tenSP.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tenSP.setText("Tên sản phẩm: ");

        txt_tenSP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tenSP.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_tenSP.setPreferredSize(new java.awt.Dimension(0, 30));

        lbl_giaMua.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_giaMua.setText("Giá mua:");

        txt_giaMua.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_giaMua.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_giaMua.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_giaMua.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_giaMuaKeyPressed(evt);
            }

            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_giaMuaKeyTyped(evt);
            }
        });

        lbl_nhaXuatBan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_nhaXuatBan.setText("Nhà xuất bản:");

        lbl_soTrang.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_soTrang.setText("Số trang:");

        lbl_NhaCungCapTop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_NhaCungCapTop.setText("Nhà cung cấp:");

        cmb_tacGiaTop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_tacGiaTop.setPreferredSize(new java.awt.Dimension(0, 30));
        cmb_tacGiaTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_tacGiaTopActionPerformed(evt);
            }
        });

        lbl_moTa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_moTa.setText("Mô tả:");

        txt_moTa.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_moTa.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_moTa.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_moTa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_moTaActionPerformed(evt);
            }
        });

        lbl_tacGiaTop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tacGiaTop.setText("Tác giả:");

        txt_vat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_vat.setEnabled(false);
        txt_vat.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_vat.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_vat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_vatActionPerformed(evt);
            }
        });

        lbl_vat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_vat.setText("VAT:");

        pnl_hinhAnh.setBackground(new java.awt.Color(255, 255, 255));

        lbl_HinhAnh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_HinhAnh.setText("Hình ảnh:");

        javax.swing.GroupLayout pnl_hinhAnhLayout = new javax.swing.GroupLayout(pnl_hinhAnh);
        pnl_hinhAnh.setLayout(pnl_hinhAnhLayout);
        pnl_hinhAnhLayout.setHorizontalGroup(
                pnl_hinhAnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_hinhAnhLayout.createSequentialGroup()
                                .addComponent(lbl_HinhAnh)
                                .addContainerGap(246, Short.MAX_VALUE))
        );
        pnl_hinhAnhLayout.setVerticalGroup(
                pnl_hinhAnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_hinhAnhLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lbl_HinhAnh)
                                .addContainerGap(86, Short.MAX_VALUE))
        );

        cmb_theLoaiTop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_theLoaiTop.setPreferredSize(new java.awt.Dimension(0, 30));
        cmb_theLoaiTop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                try {
                    cmb_theLoaiTopItemStateChanged(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        cmb_theLoaiTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_theLoaiTopActionPerformed(evt);
            }
        });
        cmb_theLoaiTop.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cmb_theLoaiTopPropertyChange(evt);
            }
        });

        cmb_nhaCungCapTop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_nhaCungCapTop.setPreferredSize(new java.awt.Dimension(0, 30));
        cmb_nhaCungCapTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_nhaCungCapTopActionPerformed(evt);
            }
        });

        txt_soTrang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_soTrang.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_soTrang.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_soTrang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_soTrangKeyPressed(evt);
            }

            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_soTrangKeyTyped(evt);
            }
        });

        cmb_nhaXuatBanTop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_nhaXuatBanTop.setPreferredSize(new java.awt.Dimension(0, 30));
        cmb_nhaXuatBanTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_nhaXuatBanTopActionPerformed(evt);
            }
        });

        txt_soLuong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_soLuong.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_soLuong.setPreferredSize(new java.awt.Dimension(0, 30));
        txt_soLuong.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_soLuongKeyPressed(evt);
            }

            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_soLuongKeyTyped(evt);
            }
        });
        lbl_HinhAnhView.setIcon(IconUtils.createScaledIcon("src/main/java/img/no_image.png", 100, 100)); // NOI18N
        lbl_HinhAnhView.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lbl_HinhAnhView.setMinimumSize(new java.awt.Dimension(200, 100));
        lbl_HinhAnhView.setPreferredSize(new java.awt.Dimension(200, 100));
        lbl_HinhAnhView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_HinhAnhViewMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnl_centerLayout = new javax.swing.GroupLayout(pnl_center);
        pnl_center.setLayout(pnl_centerLayout);
        pnl_centerLayout.setHorizontalGroup(
                pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lbl_maSP)
                                                        .addComponent(lbl_tenSP)
                                                        .addComponent(lbl_soLuong, javax.swing.GroupLayout.Alignment.TRAILING))
                                                .addGap(12, 12, 12)
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txt_maSP, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txt_tenSP, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txt_soLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                .addComponent(lbl_theLoaiTop)
                                                .addGap(12, 12, 12)
                                                .addComponent(cmb_theLoaiTop, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(1, 1, 1)))
                                .addGap(40, 40, 40)
                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                .addComponent(lbl_nhaXuatBan)
                                                .addGap(14, 14, 14)
                                                .addComponent(cmb_nhaXuatBanTop, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                .addComponent(lbl_moTa)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(txt_moTa, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lbl_giaMua)
                                                        .addComponent(lbl_NhaCungCapTop))
                                                .addGap(12, 12, 12)
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txt_giaMua, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cmb_nhaCungCapTop, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(40, 40, 40)
                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lbl_tacGiaTop)
                                                        .addComponent(lbl_soTrang)
                                                        .addComponent(lbl_vat))
                                                .addGap(12, 12, 12)
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txt_vat, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txt_soTrang, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cmb_tacGiaTop, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lbl_HinhAnhView, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(pnl_hinhAnh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                                                        .addComponent(lbl_giaMua)
                                                                        .addComponent(txt_giaMua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                .addGap(268, 268, 268))
                                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lbl_soTrang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(txt_soTrang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                .addGap(19, 19, 19)
                                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lbl_tacGiaTop, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(cmb_tacGiaTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(58, 58, 58)
                                                                .addComponent(lbl_HinhAnhView, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(pnl_hinhAnh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(69, 69, 69))))
                                        .addGroup(pnl_centerLayout.createSequentialGroup()
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txt_maSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(lbl_maSP))
                                                .addGap(18, 18, 18)
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(lbl_NhaCungCapTop, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(cmb_nhaCungCapTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txt_tenSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lbl_tenSP))
                                                .addGap(22, 22, 22)
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(txt_vat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(lbl_vat))
                                                        .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lbl_soLuong)
                                                                .addComponent(lbl_nhaXuatBan)
                                                                .addComponent(cmb_nhaXuatBanTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(txt_soLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(18, 18, 18)
                                                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txt_moTa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(lbl_moTa)
                                                        .addComponent(lbl_theLoaiTop)
                                                        .addComponent(cmb_theLoaiTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(68, 68, 68))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, 1082, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 1096, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(scr_main, javax.swing.GroupLayout.PREFERRED_SIZE, 1090, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scr_main, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_moTaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_moTaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_moTaActionPerformed

    private void cmb_tacGiaTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_tacGiaTopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_tacGiaTopActionPerformed

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
            try {
                if (validData()) {
                    themSanPham();
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
    }//GEN-LAST:event_btn_themActionPerformed

    private void btn_locSPActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btn_locSPActionPerformed
        locSanPham();
    }//GEN-LAST:event_btn_locSPActionPerformed

    private void btn_capNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_capNhatActionPerformed
        if (validData()) {
            try {
                if (updateSanPham()) {
                    NotifyToast.showSuccessToast("Cập nhật sản phẩm thành công");
                    lamMoi();
                    renderDataToView();
                    Application.getViewTaoHD().getAllSP();
                } else {
                    NotifyToast.showSuccessToast("Cập nhật sản phẩm thất bại");
                }
            } catch (SQLException ex) {
                Logger.getLogger(panel_QuanLySanPham.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }//GEN-LAST:event_btn_capNhatActionPerformed

    private void table_dsSPMouseClicked(java.awt.event.MouseEvent evt) throws RemoteException {//GEN-FIRST:event_table_dsSPMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) {
            int selectedRow = table_dsSP.getSelectedRow();
            if (selectedRow != -1) {
                String maSP = table_dsSP.getValueAt(selectedRow, 0).toString();
                SanPham sp = getSPByMa(maSP);

                txt_maSP.setText(sp.getMaSanPham());
                String giaTienChinhSua = Long.toString((long) sp.getGiaMua()).replace(" VNĐ", " ").replace(",", "");
                txt_giaMua.setText(giaTienChinhSua);
                txt_moTa.setText(sp.getMoTaSanPham());
                txt_soLuong.setText(Integer.toString(sp.getSoLuongTon()));
                txt_soTrang.setText(Integer.toString(sp.getSoTrang()));
                txt_tenSP.setText(sp.getTenSanPham());
                txt_vat.setText(Double.toString(sp.getVat()));
                cmb_nhaCungCapTop.setSelectedItem(sp.getNhaCungCap());
                cmb_nhaXuatBanTop.setSelectedItem(sp.getNhaXuatBan());
                cmb_tacGiaTop.setSelectedItem(sp.getTacGia());
                cmb_theLoaiTop.setSelectedItem(sp.getTheLoai());
                lbl_HinhAnhView.setIcon(new ImageIcon(sp.getHinhAnh()));
            }
        }

    }//GEN-LAST:event_table_dsSPMouseClicked

    private void btn_lamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lamMoiActionPerformed
        try {
            renderDataToView();
        } catch (RemoteException ex) {
            Logger.getLogger(panel_QuanLySanPham.class.getName()).log(Level.SEVERE, null, ex);
        }
        lamMoi();
    }//GEN-LAST:event_btn_lamMoiActionPerformed

    private void txt_timKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_timKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_timKiemActionPerformed

    private void txt_vatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_vatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_vatActionPerformed

    private void cmb_nhaCungCapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_nhaCungCapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_nhaCungCapActionPerformed

    private void cmb_tacGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_tacGiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_tacGiaActionPerformed

    private void cmb_theLoaiTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_theLoaiTopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_theLoaiTopActionPerformed

    private void cmb_nhaCungCapTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_nhaCungCapTopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_nhaCungCapTopActionPerformed

    private void cmb_nhaXuatBanTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_nhaXuatBanTopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_nhaXuatBanTopActionPerformed

    private void btn_danhMucActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException, MalformedURLException, NotBoundException {//GEN-FIRST:event_btn_danhMucActionPerformed
        // TODO add your handling code here:
        Application.showForm(new panel_QuanLyDanhMuc());
    }//GEN-LAST:event_btn_danhMucActionPerformed

    private void txt_timKiemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_timKiemKeyPressed
        // TODO add your handling code here:
        debounce.restart();
    }//GEN-LAST:event_txt_timKiemKeyPressed

    private void txt_soLuongKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soLuongKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_soLuongKeyPressed

    private void txt_soLuongKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soLuongKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // Không cho phép nhập ký tự khác số
        }
    }//GEN-LAST:event_txt_soLuongKeyTyped

    private void lbl_HinhAnhViewMouseClicked(java.awt.event.MouseEvent evt) {                                             
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) { // Check for double click
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("src\\main\\java\\img\\"));

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String imagePath = selectedFile.getAbsolutePath();
                ImageIcon imageIcon = new ImageIcon(imagePath);
                // Set the chosen image path to the label (you might want to load and display the image)
                lbl_HinhAnhView.setIcon(imageIcon);
            }
        }
    }

    private void cmb_theLoaiTopItemStateChanged(java.awt.event.ItemEvent evt) throws RemoteException, MalformedURLException, NotBoundException {//GEN-FIRST:event_cmb_theLoaiTopItemStateChanged
        // TODO add your handling code here:
        TheLoai obj = (TheLoai) cmb_theLoaiTop.getSelectedItem();
//        2 case -> Sách or VPP
        switch (obj.getDanhMuc().getTenDanhMuc()) {
            case "Sách" -> txt_vat.setText("0.08");
            case "Văn Phòng Phẩm" -> txt_vat.setText("0.05");
            default -> txt_vat.setText("0");
        }
        if (!cmb_theLoaiTop.getSelectedItem().toString().contains("Sách")) {
            txt_soTrang.setText("0");
            cmb_tacGiaTop.removeAllItems();
            cmb_nhaXuatBanTop.removeAllItems();
            cmb_tacGiaTop.setEnabled(false);
            txt_soTrang.setEnabled(false);
            cmb_nhaXuatBanTop.setEnabled(false);
        } else {
            NhaXuatBan_Bus nxb_Bus = (NhaXuatBan_Bus) Naming.lookup(URL + "NhaXuatBan");
            TacGia_Bus tg_Bus = (TacGia_Bus) Naming.lookup(URL + "TacGia");
            DefaultComboBoxModel<NhaXuatBan> comboBoxModelNXB = new DefaultComboBoxModel<>();
            DefaultComboBoxModel<TacGia> comboBoxModelTG = new DefaultComboBoxModel<>();
            for (NhaXuatBan i : nxb_Bus.getAllNXB()) {
                comboBoxModelNXB.addElement(i);
            }
            for (TacGia i : tg_Bus.getAllTacGia()) {
                comboBoxModelTG.addElement(i);
            }
            cmb_nhaXuatBanTop.removeAllItems();
            cmb_nhaXuatBanTop.setModel(comboBoxModelNXB);
            cmb_tacGiaTop.removeAllItems();
            cmb_tacGiaTop.setModel(comboBoxModelTG);
            cmb_tacGiaTop.setEnabled(true);
            txt_soTrang.setEnabled(true);
            cmb_nhaXuatBanTop.setEnabled(true);
        }
    }//GEN-LAST:event_cmb_theLoaiTopItemStateChanged

    private void cmb_theLoaiTopPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cmb_theLoaiTopPropertyChange
        // TODO add your handling code here:

    }//GEN-LAST:event_cmb_theLoaiTopPropertyChange

    private void txt_soTrangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soTrangKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_soTrangKeyPressed

    private void txt_giaMuaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_giaMuaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_giaMuaKeyPressed

    private void txt_giaMuaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_giaMuaKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_giaMuaKeyTyped

    private void txt_soTrangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soTrangKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_soTrangKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_capNhat;
    private javax.swing.JButton btn_danhMuc;
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_locSP;
    private javax.swing.JButton btn_them;
    private javax.swing.JComboBox<DanhMuc> cmb_danhMuc;
    private javax.swing.JComboBox<NhaCungCap> cmb_nhaCungCap;
    private javax.swing.JComboBox<NhaCungCap> cmb_nhaCungCapTop;
    private javax.swing.JComboBox<NhaXuatBan> cmb_nhaXuatBanTop;
    private javax.swing.JComboBox<TacGia> cmb_tacGia;
    private javax.swing.JComboBox<TacGia> cmb_tacGiaTop;
    private javax.swing.JComboBox<TheLoai> cmb_theLoai;
    private javax.swing.JComboBox<TheLoai> cmb_theLoaiTop;
    private javax.swing.JLabel lbl_HinhAnh;
    private javax.swing.JLabel lbl_HinhAnhView;
    private javax.swing.JLabel lbl_NhaCungCapTop;
    private javax.swing.JLabel lbl_danhMuc;
    private javax.swing.JLabel lbl_giaMua;
    private javax.swing.JLabel lbl_maSP;
    private javax.swing.JLabel lbl_moTa;
    private javax.swing.JLabel lbl_nhaCungCap;
    private javax.swing.JLabel lbl_nhaXuatBan;
    private javax.swing.JLabel lbl_soLuong;
    private javax.swing.JLabel lbl_soTrang;
    private javax.swing.JLabel lbl_tacGia;
    private javax.swing.JLabel lbl_tacGiaTop;
    private javax.swing.JLabel lbl_tenSP;
    private javax.swing.JLabel lbl_theLoai;
    private javax.swing.JLabel lbl_theLoaiTop;
    private javax.swing.JLabel lbl_vat;
    private javax.swing.JPanel pnl_center;
    private javax.swing.JPanel pnl_hinhAnh;
    private javax.swing.JPanel pnl_south;
    private javax.swing.JScrollPane scr_main;
    private javax.swing.JTable table_dsSP;
    private javax.swing.JTextField txt_giaMua;
    private javax.swing.JTextField txt_maSP;
    private javax.swing.JTextField txt_moTa;
    private javax.swing.JTextField txt_soLuong;
    private javax.swing.JTextField txt_soTrang;
    private javax.swing.JTextField txt_tenSP;
    private javax.swing.JTextField txt_timKiem;
    private javax.swing.JTextField txt_vat;
    // End of variables declaration//GEN-END:variables

}
