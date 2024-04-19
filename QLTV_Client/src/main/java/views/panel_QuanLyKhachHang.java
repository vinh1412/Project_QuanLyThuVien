package views;

import bus.KhachHang_Bus;
import entity.KhachHang;
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
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class panel_QuanLyKhachHang extends javax.swing.JPanel {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private final KhachHang_Bus khachHangBus;
    private final DefaultTableModel model;
    private final boolean internal;
    private Timer debounce;

    public panel_QuanLyKhachHang(boolean internal) throws RemoteException, MalformedURLException, NotBoundException {
        initComponents();
        customInitComponent();
        khachHangBus = (KhachHang_Bus) Naming.lookup(URL + "KhachHang");
        model = (DefaultTableModel) table_dsKH.getModel();
        this.internal = internal;
        DocDuLieuTuSQLVaoTable();
    }

    @SuppressWarnings("unchecked")
    public boolean valiData() throws RemoteException {
        String tenKH = txt_tenKH.getText().trim();
        String soDT = txt_soDienThoai.getText().trim();
        Date ngaySinhDate = cld_ngaySinh.getDate();
        LocalDate ngaySinh = ngaySinhDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayHienTai = LocalDate.now();
        Period period = Period.between(ngaySinh, ngayHienTai);
        int tuoi = period.getYears();

        if (tenKH.isEmpty() || tenKH.matches("^[A-Za-z\\p{L}  ]+$") == false) {
            NotifyToast.showErrorToast("Tên Khách Hàng Không Hợp Lệ!");
            txt_tenKH.requestFocus();
            return false;
        }
        
        if (tuoi < 7) {
            NotifyToast.showErrorToast("Khách hàng phải > 7 tuổi");
            return false;
        }

        if (!soDT.matches("(^(03)[2-9]\\d{7})|(^(07)[06-9]\\d{7})|(^(08)[1-5]\\d{7})|(^(056)\\d{7})|(^(058)\\d{7})|(^(059)\\d{7})|(^(09)[0-46-9]\\d{7})")) {
            NotifyToast.showErrorToast("Số điện thoại không hợp lệ!");
            txt_soDienThoai.requestFocus();
            return false;
        }
        if (khachHangBus.checkSDT(soDT) > 0) {
            NotifyToast.showErrorToast("Số điện thoại đã tồn tại");
            txt_soDienThoai.requestFocus();
            return false;
        }

        return true;
    }

    private void customInitComponent() {
        btn_them.setIcon(IconUtils.addIcon());
        btn_capNhat.setIcon(IconUtils.updateIcon());
        btn_lamMoi.setIcon(IconUtils.refreshIcon());
        txt_timKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm...");
        debounce = new Timer(300, (ActionEvent e) -> {
            // Timer action: Perform search after debounce time
            Tim();
        });
        debounce.setRepeats(false); // Set to non-repeating
    }

    // Cap Nhat Khach Hang
    private void capNhatKhachHang() throws RemoteException {
        int r = table_dsKH.getSelectedRow();
        if (r < 0) {
            NotifyToast.showErrorToast("Hãy chọn khách hàng cần sửa thông tin");
            return;
        }
        if (valiData()) {
            KhachHang kh = taoKhachHang();

            if (khachHangBus.capNhatKH(kh)) {
                model.removeRow(r); //xoa hang duoc chon
                model.insertRow(r, getObjectKH(kh));
                NotifyToast.showSuccessToast("Đã cập nhật thành công khách hàng");
                XoaTrang();
            } else {
                NotifyToast.showErrorToast("Đã có lỗi xảy ra, không thể cập nhật");
            }
        }

    }

    //Tao Khach Hang//
    public KhachHang taoKhachHang() {
        String maKH = txt_maKH.getText();
        String tenKH = txt_tenKH.getText();
        int gioiTinh = cmb_gioiTinh.getSelectedIndex();
        String soDT = txt_soDienThoai.getText();
        Date ngaySinh = cld_ngaySinh.getDate();
        int diemDoiThuong = Integer.parseInt(txt_diemDoiThuong.getText());

        KhachHang khachHang = new KhachHang(maKH, tenKH, gioiTinh, soDT, ngaySinh, diemDoiThuong);
        return khachHang;
    }

    //Them Khách Hàng
    public void themKhachHang() throws RemoteException {
        if (txt_maKH.getText().equals("")) {
            String maKH = generateCustomerCode();
        String tenKH = txt_tenKH.getText();
        int gioiTinh = cmb_gioiTinh.getSelectedIndex();
        String soDT = txt_soDienThoai.getText();
        int loai = 4;
        Date ngaySinh = cld_ngaySinh.getDate();
        
        KhachHang khachHang = new KhachHang(maKH, tenKH, gioiTinh, soDT, ngaySinh, 0);
        boolean themKhachHang = khachHangBus.themKhachHang(khachHang);
        if (themKhachHang) {
            if (!internal) {
                Application.setSTDKH(soDT);;
            }
            XoaHetDuLieuTrenTable();
            DocDuLieuTuSQLVaoTable();
            XoaTrang();
            NotifyToast.showSuccessToast("Thêm khách hàng thành công");
        } else {
            NotifyToast.showErrorToast("Thêm khách hàng thất bại");
        }
        } else {
            NotifyToast.showErrorToast("Khách hàng đã tồn tại");
        }

    }

    //Nam Sinh
    public String generateCustomerCode() throws RemoteException {
        // Lấy năm sinh từ ngày được chọn
        Date ngaySinh = cld_ngaySinh.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        String namSinhh = sdf.format(ngaySinh);

        String soCuoi = String.format("%02d", khachHangBus.getSoLuongKH() + 1);
        String customerCode = "KH-" + namSinhh + soCuoi;

        return customerCode;
    }

    //========================//
    public Object[] getObjectKH(KhachHang kh) {
        String gt;
        gt = switch (kh.getGioiTinh()) {
            case 0 -> "Nam";
            case 1 -> "Nữ";
            default -> "Không xác định";
        };

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String ngaySinh = dateFormat.format(kh.getNgaySinh());

        Object[] obj = {kh.getMaKhachHang(), kh.getTenKhachHang(), gt, kh.getSoDienThoai(), ngaySinh, kh.getDiemDoiThuong()};

        return obj;
    }

    // Doc du lieu tu sql vao table
    private void DocDuLieuTuSQLVaoTable() throws RemoteException {
        List<KhachHang> dsKH = khachHangBus.getAllKhachHang();

        for (KhachHang kh : dsKH) {
            model.addRow(getObjectKH(kh));
        }
    }

    //tim
    public void Tim() {
        String thongTinTim = txt_timKiem.getText().trim();
        List<KhachHang> danhSachKhachHang;
        try {
            int stt = 0;
            XoaHetDuLieuTrenTable();
            danhSachKhachHang = khachHangBus.timKhachHang(thongTinTim);
            for (KhachHang kh : danhSachKhachHang) {
                model.addRow(getObjectKH(kh));
                stt++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xoa het dư lieu tren table
    public void XoaHetDuLieuTrenTable() {
        model.setRowCount(0);
    }

    //xoa trang
    public void XoaTrang() {
        txt_maKH.setText("");
        txt_tenKH.setText("");
        txt_soDienThoai.setText("");
        txt_diemDoiThuong.setText("0");

    }

    //lam moi
    public void LamMoi() throws RemoteException {
        XoaTrang();
        txt_timKiem.setText("");
        XoaHetDuLieuTrenTable();
        DocDuLieuTuSQLVaoTable();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scr_main = new javax.swing.JScrollPane();
        table_dsKH = new javax.swing.JTable();
        pnl_top = new javax.swing.JPanel();
        pnl_thongTinKH = new javax.swing.JPanel();
        pnl_thongTin = new javax.swing.JPanel();
        lbl_maKH = new javax.swing.JLabel();
        txt_maKH = new javax.swing.JTextField();
        lbl_tenKH = new javax.swing.JLabel();
        txt_tenKH = new javax.swing.JTextField();
        lbl_gioiTinh = new javax.swing.JLabel();
        cmb_gioiTinh = new javax.swing.JComboBox<>();
        lbl_soDienThoai = new javax.swing.JLabel();
        txt_soDienThoai = new javax.swing.JTextField();
        lbl_diemDoiThuong = new javax.swing.JLabel();
        lbl_ngaySinh = new javax.swing.JLabel();
        cld_ngaySinh = new com.toedter.calendar.JDateChooser();
        txt_diemDoiThuong = new javax.swing.JTextField();
        pnl_south = new javax.swing.JPanel();
        txt_timKiem = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_capNhat = new javax.swing.JButton();
        btn_lamMoi = new javax.swing.JButton();

        setBackground(new java.awt.Color(240, 240, 240));
        setMaximumSize(new java.awt.Dimension(1100, 2147483647));
        setPreferredSize(new java.awt.Dimension(1090, 940));
        setLayout(new java.awt.BorderLayout());

        scr_main.setBackground(new java.awt.Color(255, 255, 255));
        scr_main.setEnabled(false);
        scr_main.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        scr_main.setPreferredSize(new java.awt.Dimension(2, 660));

        table_dsKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table_dsKH.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã khách hàng", "Tên khách hàng", "Giới Tính", "Số Điện Thoại", "Ngày Sinh", "Điểm đổi thưởng"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_dsKH.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        table_dsKH.setRowHeight(32);
        table_dsKH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_dsKHMouseClicked(evt);
            }
        });
        scr_main.setViewportView(table_dsKH);

        add(scr_main, java.awt.BorderLayout.CENTER);

        pnl_top.setBackground(new java.awt.Color(255, 255, 255));
        pnl_top.setPreferredSize(new java.awt.Dimension(2520, 340));

        pnl_thongTinKH.setBackground(new java.awt.Color(255, 255, 255));
        pnl_thongTinKH.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnl_thongTin.setBackground(new java.awt.Color(255, 255, 255));
        pnl_thongTin.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông Tin Khách Hàng"));
        pnl_thongTin.setMaximumSize(new java.awt.Dimension(52767, 32767));
        pnl_thongTin.setPreferredSize(new java.awt.Dimension(1520, 100));

        lbl_maKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_maKH.setText("Mã khách hàng:");

        txt_maKH.setEditable(false);
        txt_maKH.setMinimumSize(new java.awt.Dimension(68, 30));

        lbl_tenKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tenKH.setText("Tên khách hàng:");

        txt_tenKH.setMinimumSize(new java.awt.Dimension(68, 30));

        lbl_gioiTinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_gioiTinh.setText("Giới tính:");

        cmb_gioiTinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ", "Khác" }));

        lbl_soDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_soDienThoai.setText("Số điện thoại:");

        txt_soDienThoai.setMinimumSize(new java.awt.Dimension(68, 30));
        txt_soDienThoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_soDienThoaiActionPerformed(evt);
            }
        });
        txt_soDienThoai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_soDienThoaiKeyTyped(evt);
            }
        });

        lbl_diemDoiThuong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_diemDoiThuong.setText("Điểm đổi thưởng:");

        lbl_ngaySinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_ngaySinh.setText("Ngày sinh:");

        cld_ngaySinh.setDateFormatString("dd/MM/yyyy\n");
        cld_ngaySinh.setDoubleBuffered(false);
        cld_ngaySinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        txt_diemDoiThuong.setEditable(false);
        txt_diemDoiThuong.setText("0");

        javax.swing.GroupLayout pnl_thongTinLayout = new javax.swing.GroupLayout(pnl_thongTin);
        pnl_thongTin.setLayout(pnl_thongTinLayout);
        pnl_thongTinLayout.setHorizontalGroup(
            pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_thongTinLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_thongTinLayout.createSequentialGroup()
                        .addComponent(lbl_maKH)
                        .addGap(31, 31, 31)
                        .addComponent(txt_maKH, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_thongTinLayout.createSequentialGroup()
                        .addComponent(lbl_tenKH)
                        .addGap(28, 28, 28)
                        .addComponent(txt_tenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(40, 40, 40)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_gioiTinh)
                    .addComponent(lbl_ngaySinh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cld_ngaySinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_diemDoiThuong)
                    .addComponent(lbl_soDienThoai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_soDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(txt_diemDoiThuong))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        pnl_thongTinLayout.setVerticalGroup(
            pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_thongTinLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cld_ngaySinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_soDienThoai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_maKH, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                        .addComponent(lbl_maKH)
                        .addComponent(lbl_ngaySinh))
                    .addComponent(lbl_soDienThoai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(46, 46, 46)
                .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnl_thongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmb_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_diemDoiThuong, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_diemDoiThuong, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_tenKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_tenKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pnl_thongTinKH.add(pnl_thongTin, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 12, 1080, 180));

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
                try {
                    btn_themActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btn_capNhat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_capNhat.setPreferredSize(new java.awt.Dimension(140, 35));
        btn_capNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btn_capNhatActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btn_lamMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_lamMoi.setPreferredSize(new java.awt.Dimension(140, 35));
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
                .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );
        pnl_southLayout.setVerticalGroup(
            pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_southLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnl_southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_timKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_them, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_capNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_lamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_topLayout = new javax.swing.GroupLayout(pnl_top);
        pnl_top.setLayout(pnl_topLayout);
        pnl_topLayout.setHorizontalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_thongTinKH, javax.swing.GroupLayout.DEFAULT_SIZE, 1091, Short.MAX_VALUE)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_south, javax.swing.GroupLayout.DEFAULT_SIZE, 1085, Short.MAX_VALUE))
        );
        pnl_topLayout.setVerticalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_topLayout.createSequentialGroup()
                .addComponent(pnl_thongTinKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(pnl_south, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(pnl_top, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    //=============================//
    private void table_dsKHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_dsKHMouseClicked
        int r = table_dsKH.getSelectedRow(); //lay so hang

        txt_maKH.setText(table_dsKH.getValueAt(r, 0).toString());
        txt_tenKH.setText(table_dsKH.getValueAt(r, 1).toString());
        txt_soDienThoai.setText(table_dsKH.getValueAt(r, 3).toString());
        txt_diemDoiThuong.setText(table_dsKH.getValueAt(r, 5).toString());

        int gioiTinh;
        if (table_dsKH.getValueAt(r, 2).toString().equalsIgnoreCase("Nam")) {
            gioiTinh = 0;
        } else if (table_dsKH.getValueAt(r, 2).toString().equalsIgnoreCase("Nữ")) {
            gioiTinh = 1;
        } else {
            gioiTinh = 2;
        }
        cmb_gioiTinh.setSelectedIndex(gioiTinh);

        String ngaysinh = table_dsKH.getValueAt(r, 4).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            cld_ngaySinh.setDate(sdf.parse(ngaysinh));
        } catch (ParseException ex) {
            Logger.getLogger(panel_QuanLyKhachHang.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_table_dsKHMouseClicked

    private void btn_capNhatActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btn_capNhatActionPerformed
        capNhatKhachHang();
    }//GEN-LAST:event_btn_capNhatActionPerformed

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btn_themActionPerformed
        if (valiData()) {
            themKhachHang();
            if (!internal) {
                Application.getViewTaoHD().setKhachHang();
                Application.showForm(Application.getViewTaoHD());
            }
        }
    }//GEN-LAST:event_btn_themActionPerformed

    private void txt_soDienThoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_soDienThoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_soDienThoaiActionPerformed

    private void btn_lamMoiActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btn_lamMoiActionPerformed
        LamMoi();
    }//GEN-LAST:event_btn_lamMoiActionPerformed

    private void txt_timKiemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_timKiemKeyPressed
        // TODO add your handling code here:
        debounce.restart();
    }//GEN-LAST:event_txt_timKiemKeyPressed

    private void txt_timKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_timKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_timKiemActionPerformed

    private void txt_soDienThoaiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_soDienThoaiKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_soDienThoaiKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_capNhat;
    private javax.swing.JButton btn_lamMoi;
    private javax.swing.JButton btn_them;
    private com.toedter.calendar.JDateChooser cld_ngaySinh;
    private javax.swing.JComboBox<String> cmb_gioiTinh;
    private javax.swing.JLabel lbl_diemDoiThuong;
    private javax.swing.JLabel lbl_gioiTinh;
    private javax.swing.JLabel lbl_maKH;
    private javax.swing.JLabel lbl_ngaySinh;
    private javax.swing.JLabel lbl_soDienThoai;
    private javax.swing.JLabel lbl_tenKH;
    private javax.swing.JPanel pnl_south;
    private javax.swing.JPanel pnl_thongTin;
    private javax.swing.JPanel pnl_thongTinKH;
    private javax.swing.JPanel pnl_top;
    private javax.swing.JScrollPane scr_main;
    private javax.swing.JTable table_dsKH;
    private javax.swing.JTextField txt_diemDoiThuong;
    private javax.swing.JTextField txt_maKH;
    private javax.swing.JTextField txt_soDienThoai;
    private javax.swing.JTextField txt_tenKH;
    private javax.swing.JTextField txt_timKiem;
    // End of variables declaration//GEN-END:variables
}
