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
import utils.IconUtils;
import utils.RMIServiceURL;
import utils.getCommonIcons;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class panel_QuanLySanPham_NVBH extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private final SanPham_Bus sanPham_Bus;
    private Timer debounce;
    private final DefaultTableModel model;
    private List<SanPham> dsSP;

    public panel_QuanLySanPham_NVBH() throws RemoteException, MalformedURLException, NotBoundException {
        sanPham_Bus = (SanPham_Bus) Naming.lookup(URL + "SanPham");
        initComponents();
        model = (DefaultTableModel) table_dsSP.getModel();
        customInitComponents();
        renderDataToView();
        renderDataToComboBox();
    }

    private void customInitComponents() {
        txt_timKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm Kiếm...");
        btn_locSP.setIcon(IconUtils.filterIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
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
    }

    public class ImageRenderer extends DefaultTableCellRenderer {

        JLabel lbl = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            lbl.setIcon((ImageIcon) value);
            return lbl;
        }
    }

    private void renderDataToView() throws RemoteException {
        dsSP = sanPham_Bus.getAllSanPham();
        model.setRowCount(0);

        table_dsSP.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());
        for (SanPham i : dsSP) {
            ImageIcon img = new ImageIcon(i.getHinhAnh());
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedAmount = decimalFormat.format(i.getGiaMua());

            formattedAmount += " VNĐ";
            model.addRow(new Object[]{i.getMaSanPham(), img, i.getTenSanPham(), i.getTheLoai(), i.getNhaCungCap(), i.getSoLuongTon(), formattedAmount, i.getSoTrang()});
        }
    }

    private void timKiemSanPham() throws RemoteException {
        dsSP = sanPham_Bus.timKiemSanPham(txt_timKiem.getText().trim());
        model.setRowCount(0);
        for (SanPham i : dsSP) {
            ImageIcon img = new ImageIcon(i.getHinhAnh());
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedAmount = decimalFormat.format(i.getGiaMua());

            formattedAmount += " VNĐ";
            model.addRow(new Object[]{i.getMaSanPham(), img, i.getTenSanPham(), i.getTheLoai(), i.getNhaCungCap(), i.getSoLuongTon(), formattedAmount, i.getSoTrang()});
        }
    }

    private void locSanPham() throws RemoteException {
        dsSP = sanPham_Bus.locSanPham((NhaCungCap) cmb_nhaCungCap.getSelectedItem(), (TacGia) cmb_tacGia.getSelectedItem(),
                (DanhMuc) cmb_danhMuc.getSelectedItem(), (TheLoai) cmb_theLoai.getSelectedItem());
        model.setRowCount(0);
        for (SanPham i : dsSP) {
            ImageIcon img = new ImageIcon(i.getHinhAnh());
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedAmount = decimalFormat.format(i.getGiaMua());

            formattedAmount += " VNĐ";
            model.addRow(new Object[]{i.getMaSanPham(), img, i.getTenSanPham(), i.getTheLoai(), i.getNhaCungCap(), i.getSoLuongTon(), formattedAmount, i.getSoTrang()});
        }
    }

    private SanPham getSPByMa(String maSP) {
        for (SanPham i : dsSP) {
            if (i.getMaSanPham().equals(maSP)) {
                return i;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scr_main = new javax.swing.JScrollPane();
        table_dsSP = new javax.swing.JTable();
        pnl_south = new javax.swing.JPanel();
        txt_timKiem = new javax.swing.JTextField();
        btn_locSP = new javax.swing.JButton();
        lbl_nhaCungCap = new javax.swing.JLabel();
        cmb_nhaCungCap = new javax.swing.JComboBox<>();
        lbl_tacGia = new javax.swing.JLabel();
        cmb_tacGia = new javax.swing.JComboBox<>();
        lbl_danhMuc = new javax.swing.JLabel();
        cmb_danhMuc = new javax.swing.JComboBox<>();
        lbl_theLoai = new javax.swing.JLabel();
        cmb_theLoai = new javax.swing.JComboBox<>();
        btn_lamMoi = new javax.swing.JButton();

        setBackground(new java.awt.Color(240, 240, 240));

        scr_main.setBackground(new java.awt.Color(255, 255, 255));
        scr_main.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách sản phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        scr_main.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scr_main.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        scr_main.setPreferredSize(new java.awt.Dimension(1420, 600));

        table_dsSP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_dsSP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sản phẩm", "Ảnh sản phẩm", "Tên sản phẩm", "Thể loại", "Nhà cung cấp", "Số lượng", "Giá mua", "Số trang"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_dsSP.setAlignmentY(16.0F);
        table_dsSP.setAutoscrolls(false);
        table_dsSP.setRowHeight(100);
        table_dsSP.setRowMargin(16);
        table_dsSP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_dsSPMouseClicked(evt);
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

        btn_locSP.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_locSP.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_locSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_locSPActionPerformed(evt);
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

        btn_lamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btn_lamMoiActionPerformed(evt);
                } catch (RemoteException e) {
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
                        .addComponent(lbl_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(cmb_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                        .addComponent(btn_locSP, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_southLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_lamMoi)))
                .addGap(18, 18, 18))
        );
        pnl_southLayout.setVerticalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_southLayout.createSequentialGroup()
                        .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lbl_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmb_theLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(cmb_tacGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmb_nhaCungCap)
                            .addComponent(lbl_nhaCungCap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_tacGia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(1, 1, 1))
                    .addComponent(btn_locSP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_danhMuc)
                    .addComponent(cmb_danhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scr_main, javax.swing.GroupLayout.PREFERRED_SIZE, 1096, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 1102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scr_main, javax.swing.GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_locSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_locSPActionPerformed
        try {
            locSanPham();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_locSPActionPerformed

    private void table_dsSPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_dsSPMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_table_dsSPMouseClicked

    private void txt_timKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_timKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_timKiemActionPerformed

    private void cmb_nhaCungCapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_nhaCungCapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_nhaCungCapActionPerformed

    private void cmb_tacGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_tacGiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_tacGiaActionPerformed

    private void txt_timKiemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_timKiemKeyPressed
        // TODO add your handling code here:
        debounce.restart();
    }//GEN-LAST:event_txt_timKiemKeyPressed

    private void btn_lamMoiActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btn_lamMoiActionPerformed
        cmb_danhMuc.setSelectedIndex(0);
        cmb_nhaCungCap.setSelectedIndex(0);
        cmb_tacGia.setSelectedIndex(0);
        cmb_theLoai.setSelectedIndex(0);
        renderDataToView();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_locSP;
    private javax.swing.JComboBox<DanhMuc> cmb_danhMuc;
    private javax.swing.JComboBox<NhaCungCap> cmb_nhaCungCap;
    private javax.swing.JComboBox<TacGia> cmb_tacGia;
    private javax.swing.JComboBox<TheLoai> cmb_theLoai;
    private javax.swing.JLabel lbl_danhMuc;
    private javax.swing.JLabel lbl_nhaCungCap;
    private javax.swing.JLabel lbl_tacGia;
    private javax.swing.JLabel lbl_theLoai;
    private javax.swing.JPanel pnl_south;
    private javax.swing.JScrollPane scr_main;
    private javax.swing.JTable table_dsSP;
    private javax.swing.JTextField txt_timKiem;
    // End of variables declaration//GEN-END:variables

}
