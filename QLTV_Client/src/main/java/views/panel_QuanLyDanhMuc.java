package views;

import bus.DanhMuc_Bus;
import bus.TheLoai_Bus;
import entity.DanhMuc;
import entity.TheLoai;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;


public final class panel_QuanLyDanhMuc extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private Timer debounce;
    /**
     * Creates new form panel_QuanLyDanhMuc
     */
    public panel_QuanLyDanhMuc() throws RemoteException, MalformedURLException, NotBoundException {
        danhMuc_Bus = (DanhMuc_Bus) Naming.lookup(URL + "DanhMuc");
        theLoai_Bus = (TheLoai_Bus) Naming.lookup(URL + "TheLoai");
        initComponents();
        customInitComponents();
        renderDataToTable();
    }

    private void customInitComponents() {
        txt_TimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm");
        btnCapNhat.setIcon(IconUtils.updateIcon());
        btnThem.setIcon(IconUtils.addIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            try {
                timKiem();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    public void renderDataToTable() throws RemoteException {
        List<DanhMuc> dsDanhMuc = danhMuc_Bus.getAllDanhMuc();
        List<TheLoai> dsTheLoai = theLoai_Bus.getAllTheLoai();
        DefaultTableModel tbl_Model_DSTL = (DefaultTableModel) table_DSTL.getModel();
        tbl_Model_DSTL.setRowCount(0);
        for (TheLoai i : dsTheLoai) {
            tbl_Model_DSTL.addRow(new Object[]{i.getMaTheLoai(), i.getTenTheLoai(), i.getDanhMuc().getTenDanhMuc()});
        }
        renderDataToComboBox();
    }

    public void renderDataToComboBox() throws RemoteException {
        List<DanhMuc> dsDanhMuc = danhMuc_Bus.getAllDanhMuc();
        DefaultComboBoxModel<DanhMuc> comboBoxModel = new DefaultComboBoxModel<>();
//          Fake DanhMUc -> Không có
        DanhMuc fakeDanhMuc = new DanhMuc(0, "Không có");
        comboBoxModel.addElement(fakeDanhMuc);
        for (DanhMuc i : dsDanhMuc) {
            comboBoxModel.addElement(i);
        }
        cmb_danhMucCha.setModel(comboBoxModel);
    }

    public boolean addDanhMuc() throws RemoteException {
        String tenDanhMuc = txt_TenDanhMuc.getText();
        // Kiểm tra xem các trường dữ liệu có được nhập đầy đủ không
        if (tenDanhMuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã danh mục.");
            return false;
        }
        boolean isAdd = danhMuc_Bus.themDanhMuc(tenDanhMuc);
        if (isAdd) {
            renderDataToTable();
        }
        lamMoi();
        return isAdd;
    }

    public boolean addTheLoai() throws RemoteException {
        if (!txt_MaDanhMuc.getText().equals("")) {
            NotifyToast.showErrorToast("Đã tồn tại");
            return false;
        }
        String tenDanhMuc = txt_TenDanhMuc.getText();
        DanhMuc selectedItem = (DanhMuc) cmb_danhMucCha.getSelectedItem();
        // Kiểm tra xem các trường dữ liệu có được nhập đầy đủ không
        if (tenDanhMuc.isEmpty()) {
            NotifyToast.showErrorToast("Vui lòng nhập tên thể loại.");
            return false;
        }
        boolean isAdd = theLoai_Bus.themTheLoai(tenDanhMuc, selectedItem.getMaDanhMuc());
        if (isAdd) {
            renderDataToTable();
        }
        lamMoi();
        return isAdd;
    }

    public void lamMoi() {
        txt_MaDanhMuc.setText("");
        txt_TenDanhMuc.setText("");
        cmb_danhMucCha.setSelectedIndex(0);
    }

    private boolean updateTheLoai() throws SQLException, RemoteException {
        String maTheLoai = txt_MaDanhMuc.getText();
        String tenTheLoai = txt_TenDanhMuc.getText();
        DanhMuc selectedItem = (DanhMuc) cmb_danhMucCha.getSelectedItem();

        TheLoai theLoai = new TheLoai(Integer.parseInt(maTheLoai), tenTheLoai, selectedItem);
//        Thêm thông tin tác giả vào CSDL
        boolean isUpdate = theLoai_Bus.updateTheLoai(theLoai);
        // Thêm thông tin tác giả vào bảng (DefaultTableModel) => hạn chế 1 lần request đến CSDL
        if (isUpdate) {
            renderDataToTable();
        }

        // Sau khi thêm, xóa nội dung trên các trường nhập liệu
        lamMoi();

        return isUpdate;
    }

    private void timKiem() throws RemoteException {
        DefaultTableModel tbl_Model_DSTL = (DefaultTableModel) table_DSTL.getModel();
        tbl_Model_DSTL.setRowCount(0);
        String queryParams = txt_TimKiem.getText();
        List<TheLoai> dsTheLoai = theLoai_Bus.timKiemTheLoai(queryParams);
        for (TheLoai i : dsTheLoai) {
            tbl_Model_DSTL.addRow(new Object[]{i.getMaTheLoai(), i.getTenTheLoai(), i.getDanhMuc().getTenDanhMuc()});
        }
    }

    public DanhMuc getDanhMucByTen(String tenDanhMuc) throws RemoteException {
        DanhMuc fakeDanhMuc = new DanhMuc(0, "Không có");
        if (tenDanhMuc.equals("Không")) {
            return fakeDanhMuc;
        }
        List<DanhMuc> dsDanhMuc = danhMuc_Bus.getAllDanhMuc();
        for (DanhMuc i : dsDanhMuc) {
            if (i.getTenDanhMuc().equals(tenDanhMuc)) {
                return i;
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Top2 = new javax.swing.JPanel();
        pnl_Center2 = new javax.swing.JPanel();
        lbl_TieuDe2 = new javax.swing.JLabel();
        pnlCenter2 = new javax.swing.JPanel();
        txt_MaDanhMuc = new javax.swing.JTextField();
        lbl_TenDanhMuc = new javax.swing.JLabel();
        txt_TenDanhMuc = new javax.swing.JTextField();
        lbl_maDanhMuc = new javax.swing.JLabel();
        lbl_danhMucCha = new javax.swing.JLabel();
        cmb_danhMucCha = new javax.swing.JComboBox<>();
        pnl_South = new javax.swing.JPanel();
        txt_TimKiem = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        btn_lamMoi = new javax.swing.JButton();
        btn_quayLai = new javax.swing.JButton();
        scr_Main1 = new javax.swing.JScrollPane();
        table_DSTL = new javax.swing.JTable();

        setBackground(new java.awt.Color(240, 240, 240));

        pnl_Top2.setBackground(new java.awt.Color(255, 255, 255));
        pnl_Top2.setPreferredSize(new java.awt.Dimension(1520, 340));

        pnl_Center2.setBackground(new java.awt.Color(255, 255, 255));

        lbl_TieuDe2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_TieuDe2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_TieuDe2.setText("QUẢN LÝ THỂ LOẠI");

        pnlCenter2.setBackground(new java.awt.Color(255, 255, 255));
        pnlCenter2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Danh Mục", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N
        pnlCenter2.setMaximumSize(new java.awt.Dimension(52767, 32767));
        pnlCenter2.setPreferredSize(new java.awt.Dimension(1520, 100));

        txt_MaDanhMuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_MaDanhMuc.setEnabled(false);
        txt_MaDanhMuc.setMinimumSize(new java.awt.Dimension(68, 30));

        lbl_TenDanhMuc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_TenDanhMuc.setText("Tên thể loại:");

        txt_TenDanhMuc.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_TenDanhMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_TenDanhMucActionPerformed(evt);
            }
        });

        lbl_maDanhMuc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_maDanhMuc.setText("Mã thể loại:");

        lbl_danhMucCha.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_danhMucCha.setText("Danh Mục Cha");

        cmb_danhMucCha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout pnlCenter2Layout = new javax.swing.GroupLayout(pnlCenter2);
        pnlCenter2.setLayout(pnlCenter2Layout);
        pnlCenter2Layout.setHorizontalGroup(
            pnlCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCenter2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_maDanhMuc)
                    .addComponent(lbl_TenDanhMuc))
                .addGap(53, 53, 53)
                .addGroup(pnlCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_MaDanhMuc, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addComponent(txt_TenDanhMuc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(84, 84, 84)
                .addComponent(lbl_danhMucCha)
                .addGap(18, 18, 18)
                .addComponent(cmb_danhMucCha, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCenter2Layout.setVerticalGroup(
            pnlCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCenter2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_MaDanhMuc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_maDanhMuc)
                    .addComponent(lbl_danhMucCha)
                    .addComponent(cmb_danhMucCha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TenDanhMuc)
                    .addComponent(txt_TenDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout pnl_Center2Layout = new javax.swing.GroupLayout(pnl_Center2);
        pnl_Center2.setLayout(pnl_Center2Layout);
        pnl_Center2Layout.setHorizontalGroup(
            pnl_Center2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_TieuDe2, javax.swing.GroupLayout.DEFAULT_SIZE, 1084, Short.MAX_VALUE)
            .addGroup(pnl_Center2Layout.createSequentialGroup()
                .addComponent(pnlCenter2, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnl_Center2Layout.setVerticalGroup(
            pnl_Center2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Center2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_TieuDe2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCenter2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_South.setBackground(new java.awt.Color(255, 255, 255));
        pnl_South.setPreferredSize(new java.awt.Dimension(1520, 80));

        txt_TimKiem.setPreferredSize(new java.awt.Dimension(68, 35));
        txt_TimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_TimKiemKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_TimKiemKeyReleased(evt);
            }
        });

        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThem.setPreferredSize(new java.awt.Dimension(140, 35));
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btnThemActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnCapNhat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCapNhat.setPreferredSize(new java.awt.Dimension(140, 35));
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });

        btn_lamMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_lamMoi.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_lamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lamMoiActionPerformed(evt);
            }
        });

        btn_quayLai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_quayLai.setText("Quay lại");
        btn_quayLai.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_quayLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_quayLaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_SouthLayout = new javax.swing.GroupLayout(pnl_South);
        pnl_South.setLayout(pnl_SouthLayout);
        pnl_SouthLayout.setHorizontalGroup(
            pnl_SouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_SouthLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(txt_TimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_quayLai, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );
        pnl_SouthLayout.setVerticalGroup(
            pnl_SouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_SouthLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnl_SouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_quayLai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_TimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_Top2Layout = new javax.swing.GroupLayout(pnl_Top2);
        pnl_Top2.setLayout(pnl_Top2Layout);
        pnl_Top2Layout.setHorizontalGroup(
            pnl_Top2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Top2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_Top2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnl_Center2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_South, javax.swing.GroupLayout.DEFAULT_SIZE, 1084, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnl_Top2Layout.setVerticalGroup(
            pnl_Top2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Top2Layout.createSequentialGroup()
                .addComponent(pnl_Center2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(pnl_South, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        scr_Main1.setPreferredSize(new java.awt.Dimension(1420, 600));

        table_DSTL.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_DSTL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã thể loại", "Tên thể loại", "Danh mục cha"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table_DSTL.setRowHeight(32);
        table_DSTL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    table_DSTLMouseClicked(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scr_Main1.setViewportView(table_DSTL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scr_Main1, javax.swing.GroupLayout.DEFAULT_SIZE, 1095, Short.MAX_VALUE)
            .addComponent(pnl_Top2, javax.swing.GroupLayout.DEFAULT_SIZE, 1095, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Top2, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scr_Main1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_TenDanhMucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_TenDanhMucActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_TenDanhMucActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btnThemActionPerformed
        if (addTheLoai()) {
            NotifyToast.showSuccessToast("Thêm thành công.");
        }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatActionPerformed
        try {
            int selectedRow = table_DSTL.getSelectedRow();
            if (selectedRow == -1) {
                NotifyToast.showErrorToast("Chọn thể loại cần cập nhật");
                return ;
            }
            boolean isUpdate = updateTheLoai();
            if (isUpdate) {
                NotifyToast.showSuccessToast("Cập nhật thành công.");
            } else {
                NotifyToast.showErrorToast("Cập nhật thất bại.");
            }

        } catch (SQLException | RemoteException ex) {
            Logger.getLogger(panel_QuanLyTacGia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCapNhatActionPerformed

    private void btn_lamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lamMoiActionPerformed
        // TODO add your handling code here:
        lamMoi();
    }//GEN-LAST:event_btn_lamMoiActionPerformed

    private void table_DSTLMouseClicked(java.awt.event.MouseEvent evt) throws RemoteException {//GEN-FIRST:event_table_DSTLMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) {
            int selectedRow = table_DSTL.getSelectedRow();
            if (selectedRow != -1) {
                String tenTheLoai = table_DSTL.getValueAt(selectedRow, 1).toString();
                txt_MaDanhMuc.setText(table_DSTL.getValueAt(selectedRow, 0).toString());
                txt_TenDanhMuc.setText(tenTheLoai);
                DanhMuc danhMuc = getDanhMucByTen(table_DSTL.getValueAt(selectedRow, 2).toString());
                cmb_danhMucCha.setSelectedItem(danhMuc);
            }
        }
    }//GEN-LAST:event_table_DSTLMouseClicked

    private void txt_TimKiemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_TimKiemKeyPressed
        // TODO add your handling code here:
        debounce.restart();
    }//GEN-LAST:event_txt_TimKiemKeyPressed

    private void txt_TimKiemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_TimKiemKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_TimKiemKeyReleased

    private void btn_quayLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_quayLaiActionPerformed
        // TODO add your handling code here:
        Application.showForm(Application.getViewTQLSP());
    }//GEN-LAST:event_btn_quayLaiActionPerformed

    private DanhMuc_Bus danhMuc_Bus;
    private final TheLoai_Bus theLoai_Bus;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_quayLai;
    private javax.swing.JComboBox<DanhMuc> cmb_danhMucCha;
    private javax.swing.JLabel lbl_TenDanhMuc;
    private javax.swing.JLabel lbl_TieuDe2;
    private javax.swing.JLabel lbl_danhMucCha;
    private javax.swing.JLabel lbl_maDanhMuc;
    private javax.swing.JPanel pnlCenter2;
    private javax.swing.JPanel pnl_Center2;
    private javax.swing.JPanel pnl_South;
    private javax.swing.JPanel pnl_Top2;
    private javax.swing.JScrollPane scr_Main1;
    private javax.swing.JTable table_DSTL;
    private javax.swing.JTextField txt_MaDanhMuc;
    private javax.swing.JTextField txt_TenDanhMuc;
    private javax.swing.JTextField txt_TimKiem;
    // End of variables declaration//GEN-END:variables
}
