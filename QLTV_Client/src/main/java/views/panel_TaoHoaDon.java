package views;

import bus.ChiTietHoaDon_Bus;
import bus.HoaDon_Bus;
import bus.KhachHang_Bus;
import bus.SanPham_Bus;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.SanPham;
import jakarta.persistence.EntityManager;
import net.sf.jasperreports.engine.util.JRLoader;
import utils.*;
import com.formdev.flatlaf.FlatClientProperties;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
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

public class panel_TaoHoaDon extends javax.swing.JPanel {
    private static final String URL = RMIServiceURL.getDefaultURL();
    private final SanPham_Bus sanPham_Bus;
    private Timer debounce;
    private JdialogHangChoHD hangChoView;
    private List<SanPham> dsSanPham;
    private final ArrayList<SanPham> dsSanPhamTimKiem;
    private ArrayList<SanPham> gioHang;
    private final DefaultTableModel modelSP;
    private final DefaultTableModel modelGioHang;
    private double tongTien = 0;
    private final KhachHang_Bus khachHang_Bus;
    private KhachHang kh;
    private double giamGia = 0;
    private final ArrayList<Object[]> dsHDCho;
    private final HoaDon_Bus hoaDon_Bus;
    private final ChiTietHoaDon_Bus cthd_Bus;

    public panel_TaoHoaDon() throws RemoteException, MalformedURLException, NotBoundException {
        sanPham_Bus = (SanPham_Bus) Naming.lookup(URL + "SanPham");
        khachHang_Bus = (KhachHang_Bus) Naming.lookup(URL + "KhachHang");
        initComponents();
        customInitComponents();
        dsHDCho = new ArrayList<>();
        gioHang = new ArrayList<>();
        getAllSP();
        modelSP = (DefaultTableModel) tbl_dsSP.getModel();
        modelGioHang = (DefaultTableModel) tbl_gioHang.getModel();
        dsSanPhamTimKiem = new ArrayList<>();
        hoaDon_Bus = (HoaDon_Bus) Naming.lookup(URL + "HoaDon");
        cthd_Bus = (ChiTietHoaDon_Bus) Naming.lookup(URL + "ChiTietHoaDon");
    }

    public final void getAllSP() throws RemoteException {
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

    private static void openExcelFile(File file) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.OPEN)) {
            desktop.open(file);
        } else {
            System.out.println("Không thể mở file.");
        }
    }
    private void xuatHoaDon(String maHD){
        Document document = new Document(PageSize.A4);

        try {
            List<Object[]> results = cthd_Bus.getChiTietHoaDonByMaHD(maHD);
            File file = new File("src/main/java/reportImg/"+maHD+".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();


            BaseFont baseFont = BaseFont.createFont("src/main/resources/times-new-roman-14.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 16);

            Font fontHeader = new Font(baseFont, 20, Font.BOLD);
            Font fontCell = new Font(baseFont, 16, Font.BOLD);
            Locale localeVN = new Locale("vi", "VN");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN);

            Paragraph header = new Paragraph("HÓA ĐƠN BÁN HÀNG", fontHeader);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(Chunk.NEWLINE);

            double tongTienHoaDon = 0;
            if (!results.isEmpty()) {
                Object[] firstRow = results.get(0);
                String maHoaDonValue = firstRow[4].toString();
                String tenNhanVien = firstRow[3].toString();
                String ngayLap = firstRow[7].toString();
                String tenKhachHang = firstRow[6] != null ? firstRow[6].toString() : "";
                tongTienHoaDon = Double.parseDouble(firstRow[9].toString());

                PdfPTable infoTable = new PdfPTable(4);
                infoTable.setWidthPercentage(100);

                PdfPCell labelCellMaHoaDon = new PdfPCell(new Phrase("Mã hóa đơn:", fontCell));
                labelCellMaHoaDon.setBorder(Rectangle.NO_BORDER);
                infoTable.addCell(labelCellMaHoaDon);

                PdfPCell invoiceNumberCell = new PdfPCell(new Phrase(maHoaDonValue, font));
                invoiceNumberCell.setBorder(Rectangle.NO_BORDER);
                infoTable.addCell(invoiceNumberCell);

                PdfPCell labelCellNgayLap = new PdfPCell(new Phrase("Ngày lập:", fontCell));
                labelCellNgayLap.setPaddingLeft(30f);
                labelCellNgayLap.setBorder(Rectangle.NO_BORDER);
                infoTable.addCell(labelCellNgayLap);

                PdfPCell dateCell = new PdfPCell(new Phrase(ngayLap, font));
                dateCell.setPaddingLeft(30f);
                dateCell.setBorder(Rectangle.NO_BORDER);
                infoTable.addCell(dateCell);

                PdfPTable infoTable1 = new PdfPTable(4);
                infoTable1.setWidthPercentage(100);
                PdfPCell labelCellNhanvien = new PdfPCell(new Phrase("Nhân viên:", fontCell));
                labelCellNhanvien.setBorder(Rectangle.NO_BORDER);
                infoTable1.addCell(labelCellNhanvien);

                PdfPCell employeeCell = new PdfPCell(new Phrase(tenNhanVien, font));
                employeeCell.setBorder(Rectangle.NO_BORDER);
                infoTable1.addCell(employeeCell);

                PdfPCell labelCellKhachHang = new PdfPCell(new Phrase("Khách hàng:", fontCell));
                labelCellKhachHang.setPaddingLeft(30f);
                labelCellKhachHang.setBorder(Rectangle.NO_BORDER);
                infoTable1.addCell(labelCellKhachHang);

                PdfPCell customerCell = new PdfPCell(new Phrase((tenKhachHang != null ? tenKhachHang : ""), font));
                customerCell.setPaddingLeft(30f);
                customerCell.setBorder(Rectangle.NO_BORDER);
                infoTable1.addCell(customerCell);

                document.add(infoTable);
                document.add(infoTable1);
            }
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setHeaderRows(1);
            table.setSpacingBefore(30f);

            PdfPCell cell = new PdfPCell();
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            float[] columnWidths = {2f, 1f, 1.5f, 1.5f};
            table.setWidths(columnWidths);
            cell.setPhrase(new Phrase("Tên sản phẩm", fontCell));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Số lượng", fontCell));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Giá bán", fontCell));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Thành tiền", fontCell));
            table.addCell(cell);
            for (Object[] result : results) {
                String tenSanPham = result[2].toString();
                int soLuong = Integer.parseInt(result[0].toString());
                double giaBan = Double.parseDouble(result[1].toString());
                double tongTien = Double.parseDouble(result[8].toString());

                cell.setPhrase(new Phrase(tenSanPham, font));
                table.addCell(cell);
                cell.setPhrase(new Phrase(String.valueOf(soLuong), font));
                table.addCell(cell);
                String giaBanStr = currencyFormat.format(giaBan);
                cell.setPhrase(new Phrase(giaBanStr, font));
                table.addCell(cell);
                String tongTienStr = currencyFormat.format(tongTien);
                cell.setPhrase(new Phrase(tongTienStr, font));
                table.addCell(cell);
            }

            document.add(table);

            Paragraph note = new Paragraph("Giá bán đã bao gồm thuế", new Font(baseFont, 12, Font.ITALIC));
            note.setSpacingBefore(50f);
            document.add(note);

            String tienKhachDuaStr="";
            if(!txt_tienKhachDua.getText().equals("")){
                double tienKhachDua = Double.parseDouble(txt_tienKhachDua.getText());
                tienKhachDuaStr = currencyFormat.format(tienKhachDua);
            }else {
                tienKhachDuaStr = currencyFormat.format(0);
            }

            Paragraph tienKhachDuaPa = new Paragraph("Tiền khách đưa: " + tienKhachDuaStr, fontCell);


            String tienTraLaiStr="";
            if (!txt_tienTraLai.getText().equals("")) {
                double tienTraLai = Double.parseDouble(txt_tienTraLai.getText().replaceAll("\\D",""));
                tienTraLaiStr = currencyFormat.format(tienTraLai);
            }else {
                tienTraLaiStr = currencyFormat.format(0);
            }
            Paragraph tienTraLaiPa = new Paragraph("Tiền trả lại: " + tienTraLaiStr, fontCell);

            String giamGiaStr="";
            if (!txt_giamGia.getText().equals("")) {
                double giamGia = Double.parseDouble(txt_giamGia.getText().replaceAll("\\D",""));
                giamGiaStr = currencyFormat.format(giamGia);
            }else {
                giamGiaStr = currencyFormat.format(0);
            }
            Paragraph giamGiaPa = new Paragraph("Giảm giá: " + giamGiaStr, fontCell);

            String tongTienStr="";
            if (!txt_tongTien.getText().equals("")) {
                double tongTien = Double.parseDouble(txt_tongTien.getText().replaceAll("\\D",""));
                tongTienStr = currencyFormat.format(tongTien);
            }else {
                tongTienStr = currencyFormat.format(0);
            }
            Paragraph tongTienPa = new Paragraph("Tổng tiền hóa đơn: " + tongTienStr, fontCell);

            String tongTienHoaDonStr = currencyFormat.format(tongTienHoaDon);
            Paragraph footer = new Paragraph("Thanh toán: " + tongTienHoaDonStr, fontCell);
            document.add(tongTienPa);
            document.add(giamGiaPa);
            document.add(footer);
            document.add(tienKhachDuaPa);
            document.add(tienTraLaiPa);
            document.close();
            writer.close();
            System.out.println("Hóa đơn đã được tạo thành công.");

            openExcelFile(file);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private void themHDCho() throws RemoteException {
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
                Date ngayHienTai = new Date();
                long timestampHienTai = ngayHienTai.getTime();
                long timestampNgaySinh = timestampHienTai - (7L * 365L * 24L * 60L * 60L * 1000L);
                Date ngaySinh = new Date(timestampNgaySinh);
                String maKH= GenerateID.generateMaKH(khachHang_Bus.getSoLuongKH()+1, ngaySinh);
                KhachHang kh = new KhachHang(maKH, hoTen, 0, SDT, ngaySinh, 0);
                khachHang_Bus.themKhachHang(kh);
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

    private boolean timKiemKhachHang() throws RemoteException {
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
        kh = khachHang_Bus.timKiemKhachHangTheoSDT(soDT);
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
        String tenKh = txt_tenKH.getText();
        KhachHang kh;
        if (tenKh.isEmpty()) {
            kh = null;
        } else {
            kh = khachHang_Bus.getKhachHangByTen(tenKh);

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
                khachHang_Bus.capNhatKH(kh);
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
                try {
                    btn_themSPActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
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
                new Object[][]{

                },
                new String[]{
                        "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Giá bán"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tbl_dsSP.setRowHeight(32);
        scr_dsSP.setViewportView(tbl_dsSP);

        scr_gioHang.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Giỏ hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        tbl_gioHang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbl_gioHang.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Đơn giá", "Tổng tiền"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
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
                try {
                    btn_themKHActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }
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
                try {
                    btn_huyHDActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
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

    private void btn_themSPActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btn_themSPActionPerformed
        // TODO add your handling code here:
        List<SanPham> listSP = sanPham_Bus.timKiemSanPham(txt_timKiemSP.getText().trim());


        int selectedRow = tbl_dsSP.getSelectedRow();

        if (selectedRow == -1) {
            NotifyToast.showErrorToast("Chọn sản phẩm cần thêm");
            return;
        }
        String soLuong = JOptionPane.showInputDialog("Nhập số lượng sản phẩm:");
        if (soLuong != null) {
            try {
                int quantity = Integer.parseInt(soLuong);
//                System.out.println(quantity);
                if (quantity == 0 || quantity < 0) {
                    NotifyToast.showErrorToast("Số lượng sản phẩm phải lớn hơn 0");
                    return;
                }

                Object value = tbl_dsSP.getValueAt(selectedRow, 0);
//                System.out.println(value);
                String firstCellValue = String.valueOf(value);
//                System.out.println(firstCellValue);
                SanPham i = sanPham_Bus.getSanPhamTheoMa(firstCellValue);
//                System.out.println(i.getSoLuongTon());
                boolean setSuccess = setQuantitySP(i.getMaSanPham(), quantity);
//                System.out.println(i.getSoLuongTon());
                if (setSuccess) {
                    SanPham cartItem = new SanPham(i.getMaSanPham(), i.getTenSanPham(), i.getGiaMua(), i.getSoLuongTon(), i.getVat());
                    cartItem.setSoLuongTon(quantity);
                    boolean tonTai = false;
                    for (SanPham j : gioHang) {
                        if (j.getMaSanPham().equals(cartItem.getMaSanPham())) {
                            j.setSoLuongTon(j.getSoLuongTon() + quantity);
                            tonTai = true;
                            break;
                        }
                    }
                    if (!tonTai) {
                        gioHang.add(cartItem);
                    }
                    renderDataToCart();
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
                if (quantity > 0) {

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
        try {
            timKiemKhachHang();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }//GEN-LAST:event_btn_timKiemKHActionPerformed

    private void btn_themKHActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException, MalformedURLException, NotBoundException {//GEN-FIRST:event_btn_themKHActionPerformed
        // TODO add your handling code here:
        Application.showForm(new panel_QuanLyKhachHang(false));
    }//GEN-LAST:event_btn_themKHActionPerformed

    private void btn_hangChoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hangChoActionPerformed
        // TODO add your handling code here:
        if (gioHang.isEmpty()) {
            NotifyToast.showErrorToast("Giỏ hàng đang trống");
        } else {
            try {
                themHDCho();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
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
                    try {
                        kh = khachHang_Bus.timKiemKhachHangTheoSDT(SDT);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
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
                if(!txt_tienKhachDua.getText().equals("")){
                    double tienDua = Double.parseDouble(txt_tienKhachDua.getText());
                    double thanhToan = Double.parseDouble(txt_thanhToan.getText().replaceAll("\\D", ""));
                    if (tienDua < thanhToan) {
                        NotifyToast.showErrorToast("Tiền khách đưa không đủ");
                        return;
                    }
                }else {
                    NotifyToast.showErrorToast("Vui lòng nhập tiền khách đưa");
                    return;
                }

                if (taoHD()) {
                    NotifyToast.showSuccessToast("Tạo hóa đơn thành công");
                    lamMoiKH();
                    dsSanPhamTimKiem.clear();
                    gioHang.clear();
                    txt_timKiemSP.setText("");
                    tongTien = 0;
                    giamGia = 0;
                    txt_tienKhachDua.setText("");
                    txt_tienTraLai.setText("");
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
        } catch (SQLException | RemoteException ex) {
            Logger.getLogger(panel_TaoHoaDon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
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
        double tienDua = 0;
        double thanhToan = 0;
        if (!txt_tienKhachDua.getText().isEmpty()) {

            if (!txt_tienKhachDua.getText().equals(0)) {
                tienDua = Double.parseDouble(txt_tienKhachDua.getText());
            }

            if (!txt_thanhToan.getText().equals("")) {
                thanhToan = tongTien - giamGia;
            }
            txt_tienTraLai.setText(formatVND(tienDua - thanhToan + 0.001));

        } else {
            txt_tienTraLai.setText("");
        }

    }//GEN-LAST:event_txt_tienKhachDuaKeyReleased

    private void btn_huyHDActionPerformed(java.awt.event.ActionEvent evt) throws RemoteException {//GEN-FIRST:event_btn_huyHDActionPerformed
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
        btn_themSP.setIcon(IconUtils.addIcon());
        btn_lamMoiSP.setIcon(IconUtils.refreshIcon());
        btn_taoHD.setIcon(IconUtils.addIcon());
        btn_hangCho.setIcon(IconUtils.queueIcon());
        btn_themKH.setIcon(IconUtils.addIcon());
        btn_lamMoiKH.setIcon(IconUtils.refreshIcon());
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
    private EntityManager entityManager;
    // End of variables declaration//GEN-END:variables
}
