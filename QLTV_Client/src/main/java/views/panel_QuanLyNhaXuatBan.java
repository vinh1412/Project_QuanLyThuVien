package views;

import bus.NhaXuatBan_Bus;
import entity.NhaXuatBan;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import utils.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;


public final class panel_QuanLyNhaXuatBan extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private Timer debounce;
    /**
     * Creates new form panel_QuanLyKhachHang
     */
    public panel_QuanLyNhaXuatBan() throws RemoteException, MalformedURLException, NotBoundException {
        nxb_Bus = (NhaXuatBan_Bus) Naming.lookup(URL + "NhaXuatBan");
        initComponents();
        customInitComponents();
        renderDataToTable();
    }

    private void customInitComponents() {
        txt_timKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm...");
        btn_capNhat.setIcon(IconUtils.updateIcon());
        btn_them.setIcon(IconUtils.addIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            try {
                timKiemNXB();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    public void renderDataToTable() throws RemoteException {
        List<NhaXuatBan> dsNhaXuatBan = nxb_Bus.getAllNXB();
        DefaultTableModel tbl_Model_DSTG = (DefaultTableModel) table_dsNXB.getModel();
        tbl_Model_DSTG.setRowCount(0);
        for (NhaXuatBan nxb : dsNhaXuatBan) {
            tbl_Model_DSTG.addRow(new Object[]{nxb.getMaNhaXuatBan(), nxb.getTenNhaXuatBan(), nxb.getSoDienThoai(), nxb.getDiaChiNXB()});
        }
    }

    private void timKiemNXB() throws RemoteException {
        DefaultTableModel tbl_Model_DSTG = (DefaultTableModel) table_dsNXB.getModel();
        tbl_Model_DSTG.setRowCount(0);
        String queryParams = txt_timKiem.getText();
        List<NhaXuatBan> dsNXB = nxb_Bus.timKiemNXB(queryParams);
        for (NhaXuatBan nxb : dsNXB) {
            tbl_Model_DSTG.addRow(new Object[]{nxb.getMaNhaXuatBan(), nxb.getTenNhaXuatBan(), nxb.getSoDienThoai(), nxb.getDiaChiNXB()});
        }
    }

    private void lamMoiInput() {
        txt_maNXB.setText("");
        txt_tenNXB.setText("");
        txt_soDienThoai.setText("");
        txt_diaChi.setText("");
    }

    private boolean validData() {
        String tenNXB = txt_tenNXB.getText().trim();
        String sdt = txt_soDienThoai.getText().trim();
        String diaChi = txt_diaChi.getText().trim();
        if (tenNXB.equals("")) {
            NotifyToast.showErrorToast("Không được để trống");
            txt_tenNXB.requestFocus();
            return false;
        }
        if (!sdt.matches("(^(03)[2-9]\\d{7})|(^(07)[06-9]\\d{7})|(^(08)[1-5]\\d{7})|(^(056)\\d{7})|(^(058)\\d{7})|(^(059)\\d{7})|(^(09)[0-46-9]\\d{7})")) {
            NotifyToast.showErrorToast("Số điện thoại không hợp lệ");
            txt_soDienThoai.requestFocus();
            return false;
        }
        if (!diaChi.matches("^[a-zA-ZÀ-ỹ0-9 ,-]{1,100}$")) {
            NotifyToast.showErrorToast("Địa chỉ không hợp lệ");
            txt_diaChi.requestFocus();
            return false;
        }
        return true;
    }

    private boolean addNhaXuatBan() throws SQLException, RemoteException {
        DefaultTableModel tbl_Model_DSNXB = (DefaultTableModel) table_dsNXB.getModel();
        int thuTuNhaXuatBan = nxb_Bus.getThuTuNXB();
        String maNXBFromTxt = txt_maNXB.getText();
        if (!maNXBFromTxt.equals("")) {
            NotifyToast.showErrorToast("Nhà xuất bản đã tồn tại");
            return false;
        }
        String maNXB = GenerateID.generateMaNXB(thuTuNhaXuatBan);
        String tenNXB = txt_tenNXB.getText();
        String diaChi = txt_diaChi.getText();
        String soDienThoai = txt_soDienThoai.getText();
        NhaXuatBan nxb = new NhaXuatBan(maNXB, tenNXB, diaChi, soDienThoai);

        // Thêm thông tin nhà xuất bản vào CSDL
        boolean isAdd = nxb_Bus.themNXB(nxb);

        // Thêm thông tin nhà xuất bản vào bảng (DefaultTableModel) => hạn chế 1 lần request đến CSDL
        if (isAdd) {
            Object[] rowData = {maNXB, tenNXB, soDienThoai, diaChi};
            tbl_Model_DSNXB.addRow(rowData);
        }

        // Sau khi thêm, xóa nội dung trên các trường nhập liệu
        lamMoiInput();

        return isAdd;
    }

    private boolean updateNhaXuatBan() throws SQLException, RemoteException {
        String maNXB = txt_maNXB.getText();
        String tenNXB = txt_tenNXB.getText();

        String diaChi = txt_diaChi.getText();
        String soDienThoai = txt_soDienThoai.getText();

        // Kiểm tra xem các trường dữ liệu có được nhập đầy đủ không
        if (maNXB.isEmpty() || tenNXB.isEmpty() || diaChi.isEmpty() || soDienThoai.isEmpty()) {
            NotifyToast.showErrorToast("Vui lòng điền đầy đủ thông tin nhà xuất bản");
            return false;
        }
        NhaXuatBan nxb = new NhaXuatBan(maNXB, tenNXB, diaChi, soDienThoai);

        // Sửa thông tin nhà xuất bản trong CSDL
        boolean isUpdate = nxb_Bus.updateNXB(nxb);

        // Thêm thông tin nhà xuất bản vào bảng (DefaultTableModel) => hạn chế 1 lần request đến CSDL
        if (isUpdate) {
            renderDataToTable();
        }

        // Sau khi sửa, xóa nội dung trên các trường nhập liệu
        lamMoiInput();

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
        table_dsNXB = new javax.swing.JTable();
        pnl_top = new javax.swing.JPanel();
        pnl_center = new javax.swing.JPanel();
        lbl_tieuDe = new javax.swing.JLabel();
        pnl_thongTin = new javax.swing.JPanel();
        lbl_maNXB = new javax.swing.JLabel();
        txt_maNXB = new javax.swing.JTextField();
        lbl_tenNXB = new javax.swing.JLabel();
        txt_tenNXB = new javax.swing.JTextField();
        lbl_diaChi = new javax.swing.JLabel();
        lbl_soDienThoai = new javax.swing.JLabel();
        txt_soDienThoai = new javax.swing.JTextField();
        txt_diaChi = new javax.swing.JTextField();
        pnl_south = new javax.swing.JPanel();
        txt_timKiem = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_capNhat = new javax.swing.JButton();
        btn_lamMoi = new javax.swing.JButton();

        setBackground(new java.awt.Color(240, 240, 240));

        scr_main.setBackground(new java.awt.Color(255, 255, 255));
        scr_main.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr_main.setPreferredSize(new java.awt.Dimension(1420, 600));

        table_dsNXB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_dsNXB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã nhà xuất bản", "Tên nhà xuất bản", "Số điện thoại", "Địa chỉ"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        table_dsNXB.setRowHeight(32);
        table_dsNXB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_dsNXBMouseClicked(evt);
            }
        });
        scr_main.setViewportView(table_dsNXB);

        pnl_top.setBackground(new java.awt.Color(255, 255, 255));
        pnl_top.setPreferredSize(new java.awt.Dimension(1520, 340));

        lbl_tieuDe.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_tieuDe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_tieuDe.setText("QUẢN LÝ NHÀ XUẤT BẢN");

        pnl_thongTin.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Nhà Xuất Bản", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pnl_thongTin.setMaximumSize(new java.awt.Dimension(52767, 32767));
        pnl_thongTin.setPreferredSize(new java.awt.Dimension(1520, 100));

        lbl_maNXB.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_maNXB.setText("Mã Nhà Xuất Bản:");

        txt_maNXB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_maNXB.setEnabled(false);
        txt_maNXB.setMinimumSize(new java.awt.Dimension(68, 30));

        lbl_tenNXB.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tenNXB.setText("Tên nhà xuất bản");

        txt_tenNXB.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tenNXB.setMinimumSize(new java.awt.Dimension(68, 30));

        lbl_diaChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_diaChi.setText("Địa chỉ:");

        lbl_soDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_soDienThoai.setText("Số điện thoại:");

        txt_soDienThoai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_soDienThoai.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_soDienThoai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_soDienThoaiKeyTyped(evt);
            }
        });

        txt_diaChi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_diaChi.setMinimumSize(new java.awt.Dimension(68, 30));

        javax.swing.GroupLayout pnl_thongTinLayout = new javax.swing.GroupLayout(pnl_thongTin);
        pnl_thongTin.setLayout(pnl_thongTinLayout);
        pnl_thongTinLayout.setHorizontalGroup(
            pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_thongTinLayout.createSequentialGroup()
                .addContainerGap(70, Short.MAX_VALUE)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_maNXB, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_diaChi, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_diaChi, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addComponent(txt_maNXB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(106, 106, 106)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_thongTinLayout.createSequentialGroup()
                        .addComponent(lbl_soDienThoai)
                        .addGap(18, 18, 18)
                        .addComponent(txt_soDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_thongTinLayout.createSequentialGroup()
                        .addComponent(lbl_tenNXB)
                        .addGap(18, 18, 18)
                        .addComponent(txt_tenNXB, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(103, 103, 103))
        );
        pnl_thongTinLayout.setVerticalGroup(
            pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_thongTinLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_maNXB, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(lbl_maNXB)
                    .addComponent(lbl_tenNXB)
                    .addComponent(txt_tenNXB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbl_soDienThoai)
                        .addComponent(txt_soDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                    .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbl_diaChi)
                        .addComponent(txt_diaChi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout pnl_centerLayout = new javax.swing.GroupLayout(pnl_center);
        pnl_center.setLayout(pnl_centerLayout);
        pnl_centerLayout.setHorizontalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_tieuDe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnl_thongTin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1083, Short.MAX_VALUE)
        );
        pnl_centerLayout.setVerticalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_centerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_tieuDe, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_thongTin, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnl_south.setPreferredSize(new java.awt.Dimension(1520, 80));

        txt_timKiem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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

        javax.swing.GroupLayout pnl_southLayout = new javax.swing.GroupLayout(pnl_south);
        pnl_south.setLayout(pnl_southLayout);
        pnl_southLayout.setHorizontalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 241, Short.MAX_VALUE)
                .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnl_southLayout.setVerticalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_capNhat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_topLayout = new javax.swing.GroupLayout(pnl_top);
        pnl_top.setLayout(pnl_topLayout);
        pnl_topLayout.setHorizontalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 1083, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(189, 189, 189))
        );
        pnl_topLayout.setVerticalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_top, javax.swing.GroupLayout.PREFERRED_SIZE, 1105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scr_main, javax.swing.GroupLayout.PREFERRED_SIZE, 1088, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_top, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scr_main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
        try {
            if (validData()) {
                if (addNhaXuatBan()) {
                    NotifyToast.showSuccessToast("Thêm nhà xuất bản thành công");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(panel_QuanLyTacGia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_themActionPerformed

    private void btn_capNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_capNhatActionPerformed
        try {
            if (validData()) {
                if (updateNhaXuatBan()) {
                    NotifyToast.showSuccessToast("Cập nhật xuất bản thành công");
                } else {
                    NotifyToast.showErrorToast("Cập nhật nhà xuất bản thất bại");
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
        lamMoiInput();
    }//GEN-LAST:event_btn_lamMoiActionPerformed

    private void table_dsNXBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_dsNXBMouseClicked
        if (evt.getClickCount() == 1) {
            int selectedRow = table_dsNXB.getSelectedRow();
            if (selectedRow != -1) {
                String maNXB = table_dsNXB.getValueAt(selectedRow, 0).toString();
                String tenNXB = table_dsNXB.getValueAt(selectedRow, 1).toString();
                String sdt = table_dsNXB.getValueAt(selectedRow, 2).toString();
                String diaChi = table_dsNXB.getValueAt(selectedRow, 3).toString();

                lamMoiInput();
                txt_maNXB.setText(maNXB);
                txt_tenNXB.setText(tenNXB);
                txt_diaChi.setText(diaChi);
                txt_soDienThoai.setText(sdt);
            }
        }
    }//GEN-LAST:event_table_dsNXBMouseClicked

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

    private NhaXuatBan_Bus nxb_Bus;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_capNhat;
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_them;
    private javax.swing.JLabel lbl_diaChi;
    private javax.swing.JLabel lbl_maNXB;
    private javax.swing.JLabel lbl_soDienThoai;
    private javax.swing.JLabel lbl_tenNXB;
    private javax.swing.JLabel lbl_tieuDe;
    private javax.swing.JPanel pnl_center;
    private javax.swing.JPanel pnl_south;
    private javax.swing.JPanel pnl_thongTin;
    private javax.swing.JPanel pnl_top;
    private javax.swing.JScrollPane scr_main;
    private javax.swing.JTable table_dsNXB;
    private javax.swing.JTextField txt_diaChi;
    private javax.swing.JTextField txt_maNXB;
    private javax.swing.JTextField txt_soDienThoai;
    private javax.swing.JTextField txt_tenNXB;
    private javax.swing.JTextField txt_timKiem;
    // End of variables declaration//GEN-END:variables
}
