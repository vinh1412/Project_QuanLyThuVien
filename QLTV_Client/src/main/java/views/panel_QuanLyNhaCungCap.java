package views;

import bus.NhaCungCap_Bus;
import entity.NhaCungCap;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

import utils.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.ActionEvent;
import javax.swing.Timer;


public class panel_QuanLyNhaCungCap extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private Timer debounce;
    /**
     * Creates new form panel_QuanLyKhachHang
     */
    public panel_QuanLyNhaCungCap() throws RemoteException, MalformedURLException, NotBoundException {
        ncc_Bus = (NhaCungCap_Bus) Naming.lookup(URL + "NhaCungCap");
        initComponents();
        customInitComponents();
        renderDataToTable();
    }

    private void customInitComponents() {
        txt_timKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm");
        btn_capNhat.setIcon(IconUtils.updateIcon());
        btn_them.setIcon(IconUtils.addIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            try {
                timKiemNCC();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    private void renderDataToTable() throws RemoteException {
        List<NhaCungCap> dsNhaCungCap = ncc_Bus.getAllNCC();
        DefaultTableModel tbl_Model_DSNCC = (DefaultTableModel) table_dsNCC.getModel();
        tbl_Model_DSNCC.setRowCount(0);
        for (NhaCungCap i : dsNhaCungCap) {
            tbl_Model_DSNCC.addRow(new Object[]{i.getMaNhaCungCap(), i.getTenNhaCungCap(), i.getDiaChi(), i.getSoDienThoai()});
        }
    }

    private void timKiemNCC() throws RemoteException {
        DefaultTableModel tbl_Model_DSNCC = (DefaultTableModel) table_dsNCC.getModel();
        tbl_Model_DSNCC.setRowCount(0);
        String queryParams = txt_timKiem.getText();
        List<NhaCungCap> dsNCC = ncc_Bus.timKiemNCC(queryParams);
        for (NhaCungCap i : dsNCC) {
            tbl_Model_DSNCC.addRow(new Object[]{i.getMaNhaCungCap(), i.getTenNhaCungCap(), i.getDiaChi(), i.getSoDienThoai()});
        }
    }

    private void lamMoi() {
        txt_maNCC.setText("");
        txt_diaChi.setText("");
        txt_tenNCC.setText("");
        txt_soDienThoai.setText("");
    }

    private boolean validData() {
        String tenNCC = txt_tenNCC.getText().trim();
        String sdt = txt_soDienThoai.getText().trim();
        String diaChi = txt_diaChi.getText().trim();
        if (!diaChi.matches("^[a-zA-ZÀ-ỹ0-9 -./,]{1,100}$")) {
            txt_diaChi.requestFocus();
            NotifyToast.showErrorToast("Địa chỉ không hợp lệ");
            return false;
        }
        if (!sdt.matches("(^(03)[2-9]\\d{7})|(^(07)[06-9]\\d{7})|(^(08)[1-5]\\d{7})|(^(056)\\d{7})|(^(058)\\d{7})|(^(059)\\d{7})|(^(09)[0-46-9]\\d{7})")) {
            txt_soDienThoai.requestFocus();
            NotifyToast.showErrorToast("Số điện thoại không hợp lệ");
            return false;
        }
        if (tenNCC.equals("")) {
            txt_tenNCC.requestFocus();
            NotifyToast.showErrorToast("Tên nhà cung cấp không hợp lệ");
            return false;
        }
        return true;
    }

    private boolean addNhaCungCap() throws SQLException, RemoteException {
        DefaultTableModel tbl_Model_DSNCC = (DefaultTableModel) table_dsNCC.getModel();
        int thuTuNhaCungCap = ncc_Bus.getThuTuNCC();
        String maNCC = GenerateID.generateMaNCC(thuTuNhaCungCap);
        String tenNCC = txt_tenNCC.getText();
        String diaChi = txt_diaChi.getText();
        String soDienThoai = txt_soDienThoai.getText();

        String maNXBFromTxt = txt_maNCC.getText();
        if (!maNXBFromTxt.equals("")) {
            NotifyToast.showErrorToast("Nhà cung cấp đã tồn tại");
            return false;
        }
        // Kiểm tra xem các trường dữ liệu có được nhập đầy đủ không
        if (maNCC.isEmpty() || tenNCC.isEmpty() || diaChi.isEmpty() || soDienThoai.isEmpty()) {
            NotifyToast.showErrorToast("Vui lòng điền đầy đủ thông tin nhà cung cấp");
            return false;
        }
        NhaCungCap ncc = new NhaCungCap(maNCC, tenNCC, diaChi, soDienThoai);

        // Thêm thông tin nhà xuất bản vào CSDL
        boolean isAdd = ncc_Bus.themNCC(ncc);

        // Thêm thông tin nhà xuất bản vào bảng (DefaultTableModel) => hạn chế 1 lần request đến CSDL
        if (isAdd) {
            Object[] rowData = {maNCC, tenNCC, diaChi, soDienThoai};
            tbl_Model_DSNCC.addRow(rowData);
        }

        // Sau khi thêm, xóa nội dung trên các trường nhập liệu
        lamMoi();

        return isAdd;
    }

    private boolean updateNhaCungCap() throws SQLException, RemoteException {
        String tenNCC = txt_tenNCC.getText();
        String diaChi = txt_diaChi.getText();
        String soDienThoai = txt_soDienThoai.getText();
        String maNCC = txt_maNCC.getText();

        NhaCungCap ncc = new NhaCungCap(maNCC, tenNCC, diaChi, soDienThoai);

        // Sửa thông tin nhà xuất bản trong CSDL
        boolean isUpdate = ncc_Bus.updateNCC(ncc);
        // Thêm thông tin nhà xuất bản vào bảng (DefaultTableModel) => hạn chế 1 lần request đến CSDL
        if (isUpdate) {
            renderDataToTable();
        }

        // Sau khi thêm, xóa nội dung trên các trường nhập liệu
        lamMoi();

        return isUpdate;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scr_main = new javax.swing.JScrollPane();
        table_dsNCC = new javax.swing.JTable();
        pnl_top = new javax.swing.JPanel();
        pnl_center = new javax.swing.JPanel();
        lbl_tieuDe = new javax.swing.JLabel();
        pnl_thongTin = new javax.swing.JPanel();
        lbl_maNCC = new javax.swing.JLabel();
        txt_maNCC = new javax.swing.JTextField();
        lbl_tenNCC = new javax.swing.JLabel();
        txt_tenNCC = new javax.swing.JTextField();
        lbl_diaChi = new javax.swing.JLabel();
        lbl_soDienThoai = new javax.swing.JLabel();
        txt_soDienThoai = new javax.swing.JTextField();
        txt_diaChi = new javax.swing.JTextField();
        pnl_south = new javax.swing.JPanel();
        txt_timKiem = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_capNhat = new javax.swing.JButton();
        btn_lamMoi = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1276, 978));

        scr_main.setPreferredSize(new java.awt.Dimension(1420, 600));

        table_dsNCC.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_dsNCC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Nhà Cung Cấp", "Tên Nhà Cung Cấp", "Địa Chỉ", "Số Điện Thoại"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        table_dsNCC.setRowHeight(32);
        table_dsNCC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_dsNCCMouseClicked(evt);
            }
        });
        scr_main.setViewportView(table_dsNCC);

        pnl_top.setPreferredSize(new java.awt.Dimension(1520, 340));

        lbl_tieuDe.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_tieuDe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_tieuDe.setText("QUẢN LÝ NHÀ CUNG CẤP");

        pnl_thongTin.setBackground(new java.awt.Color(255, 255, 255));
        pnl_thongTin.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Nhà Cung Cấp", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pnl_thongTin.setMaximumSize(new java.awt.Dimension(52767, 32767));
        pnl_thongTin.setPreferredSize(new java.awt.Dimension(1520, 100));

        lbl_maNCC.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_maNCC.setText("Mã Nhà Cung Cấp:");

        txt_maNCC.setEnabled(false);
        txt_maNCC.setMinimumSize(new java.awt.Dimension(68, 30));

        lbl_tenNCC.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tenNCC.setText("Tên Nhà Cung Cấp:");

        txt_tenNCC.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_tenNCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tenNCCActionPerformed(evt);
            }
        });

        lbl_diaChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_diaChi.setText("Địa Chỉ:");

        lbl_soDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_soDienThoai.setText("Số điện thoại:");

        txt_soDienThoai.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_soDienThoai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_soDienThoaiKeyTyped(evt);
            }
        });

        txt_diaChi.setMinimumSize(new java.awt.Dimension(68, 30));

        javax.swing.GroupLayout pnl_thongTinLayout = new javax.swing.GroupLayout(pnl_thongTin);
        pnl_thongTin.setLayout(pnl_thongTinLayout);
        pnl_thongTinLayout.setHorizontalGroup(
            pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_thongTinLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_maNCC, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_tenNCC, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(48, 48, 48)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txt_tenNCC, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addComponent(txt_maNCC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(156, 156, 156)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_soDienThoai)
                    .addComponent(lbl_diaChi))
                .addGap(18, 18, 18)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_soDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_diaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_thongTinLayout.setVerticalGroup(
            pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_thongTinLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_maNCC, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(lbl_maNCC)
                    .addComponent(lbl_soDienThoai)
                    .addComponent(txt_soDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbl_diaChi)
                        .addComponent(txt_diaChi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_tenNCC, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_tenNCC)))
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout pnl_centerLayout = new javax.swing.GroupLayout(pnl_center);
        pnl_center.setLayout(pnl_centerLayout);
        pnl_centerLayout.setHorizontalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_centerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_centerLayout.createSequentialGroup()
                        .addGap(0, 24, Short.MAX_VALUE)
                        .addComponent(lbl_tieuDe, javax.swing.GroupLayout.PREFERRED_SIZE, 1066, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_centerLayout.createSequentialGroup()
                        .addComponent(pnl_thongTin, javax.swing.GroupLayout.DEFAULT_SIZE, 1264, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        pnl_centerLayout.setVerticalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_centerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_tieuDe, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_thongTin, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnl_south.setBackground(new java.awt.Color(255, 255, 255));
        pnl_south.setPreferredSize(new java.awt.Dimension(1520, 80));

        txt_timKiem.setPreferredSize(new java.awt.Dimension(68, 35));
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

        javax.swing.GroupLayout pnl_southLayout = new javax.swing.GroupLayout(pnl_south);
        pnl_south.setLayout(pnl_southLayout);
        pnl_southLayout.setHorizontalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        pnl_southLayout.setVerticalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_topLayout = new javax.swing.GroupLayout(pnl_top);
        pnl_top.setLayout(pnl_topLayout);
        pnl_topLayout.setHorizontalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_south, javax.swing.GroupLayout.DEFAULT_SIZE, 1276, Short.MAX_VALUE)
            .addComponent(pnl_center, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnl_topLayout.setVerticalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_top, javax.swing.GroupLayout.DEFAULT_SIZE, 1276, Short.MAX_VALUE)
            .addComponent(scr_main, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_top, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scr_main, javax.swing.GroupLayout.PREFERRED_SIZE, 638, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_tenNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_tenNCCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tenNCCActionPerformed

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
        try {
            if (validData()) {
                if (addNhaCungCap()) {
                    NotifyToast.showSuccessToast("Thêm nhà cung cấp thành công.");
                } else {
                    NotifyToast.showErrorToast("Thêm nhà cung cấp thất bại.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(panel_QuanLyTacGia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_themActionPerformed

    private void table_dsNCCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_dsNCCMouseClicked
        if (evt.getClickCount() == 1) {
            int selectedRow = table_dsNCC.getSelectedRow();
            if (selectedRow != -1) {
                String maNCC = table_dsNCC.getValueAt(selectedRow, 0).toString();
                String tenNCC = table_dsNCC.getValueAt(selectedRow, 1).toString();
                String diaChi = table_dsNCC.getValueAt(selectedRow, 2).toString();
                String soDienThoai = table_dsNCC.getValueAt(selectedRow, 3).toString();

                lamMoi();
                txt_maNCC.setText(maNCC);
                txt_tenNCC.setText(tenNCC);
                txt_soDienThoai.setText(soDienThoai);
                txt_diaChi.setText(diaChi);
            }
        }
    }//GEN-LAST:event_table_dsNCCMouseClicked

    private void btn_capNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_capNhatActionPerformed
        try {
            if (validData()) {
                if (updateNhaCungCap()) {
                    NotifyToast.showSuccessToast("Cập nhật nhà cung cấp thành công.");
                } else {
                    NotifyToast.showErrorToast("Cập nhật nhà cung cấp thất bại.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(panel_QuanLyTacGia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_capNhatActionPerformed

    private void btn_lamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lamMoiActionPerformed
        // TODO add your handling code here:
        lamMoi();
    }//GEN-LAST:event_btn_lamMoiActionPerformed

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

    private final NhaCungCap_Bus ncc_Bus;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_capNhat;
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_them;
    private javax.swing.JLabel lbl_diaChi;
    private javax.swing.JLabel lbl_maNCC;
    private javax.swing.JLabel lbl_soDienThoai;
    private javax.swing.JLabel lbl_tenNCC;
    private javax.swing.JLabel lbl_tieuDe;
    private javax.swing.JPanel pnl_center;
    private javax.swing.JPanel pnl_south;
    private javax.swing.JPanel pnl_thongTin;
    private javax.swing.JPanel pnl_top;
    private javax.swing.JScrollPane scr_main;
    private javax.swing.JTable table_dsNCC;
    private javax.swing.JTextField txt_diaChi;
    private javax.swing.JTextField txt_maNCC;
    private javax.swing.JTextField txt_soDienThoai;
    private javax.swing.JTextField txt_tenNCC;
    private javax.swing.JTextField txt_timKiem;
    // End of variables declaration//GEN-END:variables
}
