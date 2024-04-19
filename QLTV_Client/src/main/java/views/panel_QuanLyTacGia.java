package views;

import bus.TacGia_Bus;
import entity.TacGia;

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

public final class panel_QuanLyTacGia extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private Timer debounce;

    /**
     * Creates new form panel_QuanLyKhachHang
     */
    public panel_QuanLyTacGia() throws RemoteException, MalformedURLException, NotBoundException {
        tg_Bus = (TacGia_Bus) Naming.lookup(URL + "TacGia");
        initComponents();
        customInitComponents();
        renderDataToTable();
    }

    public void renderDataToTable() throws RemoteException {
        List<TacGia> dsTacGia = tg_Bus.getAllTacGia();
        DefaultTableModel tbl_Model_DSTG = (DefaultTableModel) table_dsTG.getModel();
        tbl_Model_DSTG.setRowCount(0);
        for (TacGia tacGia : dsTacGia) {
            String gioiTinh = tacGia.getGioiTinh() == 0 ? "Nam" : "Nữ";
            tbl_Model_DSTG.addRow(new Object[]{tacGia.getMaTacGia(), tacGia.getTenTacGia(), gioiTinh, tacGia.getSoDienThoai()});
        }
    }

    private void customInitComponents() {
        txt_timKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm...");
        btn_them.setIcon(IconUtils.addIcon());
        btn_capNhat.setIcon(IconUtils.updateIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            try {
                timKiemTacGia();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    private void lamMoi() {
        txt_maTG.setText("");
        txt_soDienThoai.setText("");
        txt_tenTG.setText("");
        cmb_gioiTinh.setSelectedIndex(0);
    }

    public boolean validData() {
        String tenTG = txt_tenTG.getText().trim();
        String sdt = txt_soDienThoai.getText().trim();
        if (!tenTG.matches("^[^\\d\\s0-9!@#$%^&*(),.?\":{}|<>]+$|^[\\p{L}À-ỹ\\s]+$|^[\\p{L}À-ỹ\\s]+$|^[\\p{L}À-ỹ\\s]+$|^[a-zA-Z\\s]+$")) {
            NotifyToast.showErrorToast("Tên tác giả không hợp lệ");
            txt_tenTG.requestFocus();
            return false;
        }

        if (!sdt.matches("(^(03)[2-9]\\d{7})|(^(07)[06-9]\\d{7})|(^(08)[1-5]\\d{7})|(^(056)\\d{7})|(^(058)\\d{7})|(^(059)\\d{7})|(^(09)[0-46-9]\\d{7})")) {
            NotifyToast.showErrorToast("Số điện thoại không hợp lệ");
            txt_soDienThoai.requestFocus();
            return false;
        }
        return true;

    }

    private boolean addTacGia() throws SQLException, RemoteException {
        DefaultTableModel tbl_Model_DSTG = (DefaultTableModel) table_dsTG.getModel();
        int thuTuTacGia = tg_Bus.getThuTuTacGia();
        String maTacGia = GenerateID.generateMaTacGia(thuTuTacGia);
        String tenTacGia = txt_tenTG.getText();
        String soDienThoai = txt_soDienThoai.getText();
        String gioiTinhStr = (String) cmb_gioiTinh.getSelectedItem(); // Lấy giá trị từ combobox
        int gioiTinh = gioiTinhStr.equals("Nam") ? 0 : 1;

        String maTGFromTxt = txt_maTG.getText();
        if (!maTGFromTxt.equals("")) {
            NotifyToast.showErrorToast("Tác giả đã tồn tại");
            return false;
        }
        TacGia tg = new TacGia(maTacGia, tenTacGia, soDienThoai, gioiTinh);
//        Thêm thông tin tác giả vào CSDL
        boolean isAdd = tg_Bus.themTacGia(tg);
        // Thêm thông tin tác giả vào bảng (DefaultTableModel) => hạn chế 1 lần request đến CSDL
        if (isAdd) {
            Object[] rowData = {maTacGia, tenTacGia, gioiTinhStr, soDienThoai};
            tbl_Model_DSTG.addRow(rowData);
        }

        // Sau khi thêm, xóa nội dung trên các trường nhập liệu
        lamMoi();

        return isAdd;
    }

    private boolean updateTacGia() throws SQLException, RemoteException {
        String maTacGia = txt_maTG.getText();
        String tenTacGia = txt_tenTG.getText();
        String soDienThoai = txt_soDienThoai.getText();
        String gioiTinhStr = (String) cmb_gioiTinh.getSelectedItem(); // Lấy giá trị từ combobox
        int gioiTinh = gioiTinhStr.equals("Nam") ? 0 : 1;

        // Kiểm tra xem các trường dữ liệu có được nhập đầy đủ không
        if (maTacGia.isEmpty() || tenTacGia.isEmpty() || soDienThoai.isEmpty() || gioiTinhStr.isEmpty()) {
            NotifyToast.showErrorToast("Vui lòng điền đầy đủ thông tin tác giả");
            return false;
        }
        TacGia tg = new TacGia(maTacGia, tenTacGia, soDienThoai, gioiTinh);
//        Thêm thông tin tác giả vào CSDL
        boolean isUpdate = tg_Bus.updateTacGia(tg);
        // Thêm thông tin tác giả vào bảng (DefaultTableModel) => hạn chế 1 lần request đến CSDL
        if (isUpdate) {
            renderDataToTable();
        }

        // Sau khi thêm, xóa nội dung trên các trường nhập liệu
        lamMoi();

        return isUpdate;
    }

    private void timKiemTacGia() throws RemoteException {
        DefaultTableModel tbl_Model_DSTG = (DefaultTableModel) table_dsTG.getModel();
        tbl_Model_DSTG.setRowCount(0);
        String queryParams = txt_timKiem.getText();
        List<TacGia> dsTG = tg_Bus.timKiemTacGia(queryParams);
        for (TacGia tacGia : dsTG) {
            String gioiTinh = tacGia.getGioiTinh() == 0 ? "Nam" : "Nữ";
            tbl_Model_DSTG.addRow(new Object[]{tacGia.getMaTacGia(), tacGia.getTenTacGia(), tacGia.getSoDienThoai(), gioiTinh});
        }
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
        table_dsTG = new javax.swing.JTable();
        pnl_top = new javax.swing.JPanel();
        pnl_center = new javax.swing.JPanel();
        lbl_tieuDe = new javax.swing.JLabel();
        pnl_thongTinTG = new javax.swing.JPanel();
        lbl_maTG = new javax.swing.JLabel();
        txt_maTG = new javax.swing.JTextField();
        lbl_tenTG = new javax.swing.JLabel();
        txt_tenTG = new javax.swing.JTextField();
        lbl_gioiTinh = new javax.swing.JLabel();
        cmb_gioiTinh = new javax.swing.JComboBox<>();
        lbl_soDienThoai = new javax.swing.JLabel();
        txt_soDienThoai = new javax.swing.JTextField();
        pnl_south = new javax.swing.JPanel();
        txt_timKiem = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_capNhat = new javax.swing.JButton();
        btn_lamMoi = new javax.swing.JButton();

        setBackground(new java.awt.Color(240, 240, 240));

        scr_main.setBackground(new java.awt.Color(255, 255, 255));

        table_dsTG.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_dsTG.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Tác Giả", "Tên Tác Giả", "Giới Tính", "Số Điện Thoại"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        table_dsTG.setRowHeight(32);
        table_dsTG.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_dsTGMouseClicked(evt);
            }
        });
        scr_main.setViewportView(table_dsTG);

        pnl_top.setPreferredSize(new java.awt.Dimension(1520, 340));

        lbl_tieuDe.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_tieuDe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_tieuDe.setText("QUẢN LÝ TÁC GIẢ");

        pnl_thongTinTG.setBackground(new java.awt.Color(255, 255, 255));
        pnl_thongTinTG.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Tác Giả", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pnl_thongTinTG.setMaximumSize(new java.awt.Dimension(52767, 32767));
        pnl_thongTinTG.setPreferredSize(new java.awt.Dimension(1520, 100));

        lbl_maTG.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_maTG.setText("Mã tác giả:");

        txt_maTG.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_maTG.setEnabled(false);
        txt_maTG.setMinimumSize(new java.awt.Dimension(68, 30));

        lbl_tenTG.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tenTG.setText("Tên tác giả:");

        txt_tenTG.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_tenTG.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_tenTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tenTGActionPerformed(evt);
            }
        });

        lbl_gioiTinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_gioiTinh.setText("Giới tính:");

        cmb_gioiTinh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_gioiTinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ", "Khác" }));
        cmb_gioiTinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_gioiTinhActionPerformed(evt);
            }
        });

        lbl_soDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_soDienThoai.setText("Số điện thoại:");

        txt_soDienThoai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_soDienThoai.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_soDienThoai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_soDienThoaiKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_soDienThoaiKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout pnl_thongTinTGLayout = new javax.swing.GroupLayout(pnl_thongTinTG);
        pnl_thongTinTG.setLayout(pnl_thongTinTGLayout);
        pnl_thongTinTGLayout.setHorizontalGroup(
            pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_thongTinTGLayout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_maTG, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_tenTG, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txt_tenTG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_maTG, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_soDienThoai)
                    .addComponent(lbl_gioiTinh))
                .addGap(18, 18, 18)
                .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_soDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(133, 133, 133))
        );
        pnl_thongTinTGLayout.setVerticalGroup(
            pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_thongTinTGLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_thongTinTGLayout.createSequentialGroup()
                        .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_soDienThoai)
                            .addComponent(txt_soDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                        .addGap(42, 42, 42)
                        .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmb_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_gioiTinh)))
                    .addGroup(pnl_thongTinTGLayout.createSequentialGroup()
                        .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_maTG, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                            .addComponent(lbl_maTG))
                        .addGap(42, 42, 42)
                        .addGroup(pnl_thongTinTGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_tenTG)
                            .addComponent(txt_tenTG, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(38, 38, 38))
        );

        javax.swing.GroupLayout pnl_centerLayout = new javax.swing.GroupLayout(pnl_center);
        pnl_center.setLayout(pnl_centerLayout);
        pnl_centerLayout.setHorizontalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_tieuDe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnl_centerLayout.createSequentialGroup()
                .addComponent(pnl_thongTinTG, javax.swing.GroupLayout.PREFERRED_SIZE, 1072, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        pnl_centerLayout.setVerticalGroup(
            pnl_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_centerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_tieuDe, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_thongTinTG, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
        );

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
                .addContainerGap()
                .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 267, Short.MAX_VALUE)
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
                    .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_topLayout = new javax.swing.GroupLayout(pnl_top);
        pnl_top.setLayout(pnl_topLayout);
        pnl_topLayout.setHorizontalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_topLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 1085, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(186, 186, 186))
        );
        pnl_topLayout.setVerticalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addComponent(pnl_center, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnl_south, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_top, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1106, Short.MAX_VALUE)
            .addComponent(scr_main, javax.swing.GroupLayout.PREFERRED_SIZE, 1090, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_top, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scr_main, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_tenTGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_tenTGActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tenTGActionPerformed

    private void cmb_gioiTinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_gioiTinhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_gioiTinhActionPerformed

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
        try {
            if (validData()) {
                if (addTacGia()) {
                    NotifyToast.showSuccessToast("Thêm tác giả thành công");
                } else {
                    NotifyToast.showSuccessToast("Thêm tác giả thất bại");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(panel_QuanLyTacGia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_themActionPerformed

    private void table_dsTGMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_dsTGMouseClicked
        if (evt.getClickCount() == 1) {
            int selectedRow = table_dsTG.getSelectedRow();
            if (selectedRow != -1) {
                String maTacGia = table_dsTG.getValueAt(selectedRow, 0).toString();
                String tenTacGia = table_dsTG.getValueAt(selectedRow, 1).toString();
                String soDienThoai = table_dsTG.getValueAt(selectedRow, 3).toString();
                String gioiTinh = table_dsTG.getValueAt(selectedRow, 2).toString();

                lamMoi();
                txt_maTG.setText(maTacGia);
                txt_tenTG.setText(tenTacGia);
                txt_soDienThoai.setText(soDienThoai);
                cmb_gioiTinh.setSelectedItem(gioiTinh);
            }
        }
    }//GEN-LAST:event_table_dsTGMouseClicked

    private void btn_capNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_capNhatActionPerformed
        try {
            if (validData()) {
                if (updateTacGia()) {
                    NotifyToast.showSuccessToast("Cập nhật tác giả thành công");
                } else {
                    NotifyToast.showErrorToast("Cập nhật tác giả thất bại");
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

    private void txt_soDienThoaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soDienThoaiKeyPressed

    }//GEN-LAST:event_txt_soDienThoaiKeyPressed

    private void txt_soDienThoaiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soDienThoaiKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // Không cho phép nhập ký tự khác số
        }
    }//GEN-LAST:event_txt_soDienThoaiKeyTyped

//  Khai báo biến
    private final TacGia_Bus tg_Bus;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_capNhat;
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_them;
    private javax.swing.JComboBox<String> cmb_gioiTinh;
    private javax.swing.JLabel lbl_gioiTinh;
    private javax.swing.JLabel lbl_maTG;
    private javax.swing.JLabel lbl_soDienThoai;
    private javax.swing.JLabel lbl_tenTG;
    private javax.swing.JLabel lbl_tieuDe;
    private javax.swing.JPanel pnl_center;
    private javax.swing.JPanel pnl_south;
    private javax.swing.JPanel pnl_thongTinTG;
    private javax.swing.JPanel pnl_top;
    private javax.swing.JScrollPane scr_main;
    private javax.swing.JTable table_dsTG;
    private javax.swing.JTextField txt_maTG;
    private javax.swing.JTextField txt_soDienThoai;
    private javax.swing.JTextField txt_tenTG;
    private javax.swing.JTextField txt_timKiem;
    // End of variables declaration//GEN-END:variables
}
