package views;

import bus.TaiKhoan_Bus;
import entity.TaiKhoan;
import utils.NotifyToast;
import utils.RMIServiceURL;
import views.components.LoginForm;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import raven.toast.Notifications;

public class Application extends javax.swing.JFrame {

    private static final String URL = RMIServiceURL.getDefaultURL();
    private static String sdtKH;
    private static Application app;
    private final mainViewNVBH mainForm;
    private final mainViewNVQL mainForm1;
    private final LoginForm loginForm;
    private static TaiKhoan_Bus tk_Bus;
    private static TaiKhoan tk;
    private static panel_TaoHoaDon viewTaoHoaDon;
    private static panel_QuanLySanPham viewQLSP;
    private static int type;

    public Application() throws RemoteException, MalformedURLException, NotBoundException {
        tk_Bus = (TaiKhoan_Bus) Naming.lookup(URL + "TaiKhoan");
        initComponents();
        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);
        viewTaoHoaDon = new panel_TaoHoaDon();
        viewQLSP = new panel_QuanLySanPham();
        mainForm = new mainViewNVBH();
        mainForm1 = new mainViewNVQL();
        loginForm = new LoginForm();
        setContentPane(loginForm);
        Notifications.getInstance().setJFrame(this);
    }

    public static TaiKhoan getTK() {
        return tk;
    }

    public static void setSTDKH(String sdt) {
        sdtKH = sdt;
    }

    public static String getSTDKH() {
        return sdtKH;
    }

    public static panel_TaoHoaDon getViewTaoHD() {
        return viewTaoHoaDon;
    }

    public static panel_QuanLySanPham getViewTQLSP() {
        return viewQLSP;
    }

    public static void showForm(Component component) {
        component.applyComponentOrientation(app.getComponentOrientation());
        if (type == 0) {
            app.mainForm1.showForm(component);
        } else if (type == 1) {
            app.mainForm.showForm(component);
        }
    }

    public static void login(String userName, String password) throws RemoteException, MalformedURLException, NotBoundException {
        tk = tk_Bus.getTaiKhoanByTen(userName);
        if (tk == null) {
            NotifyToast.showErrorToast("Tài khoản không tồn tại");
        } else {
            if (tk.getMatKhau().equals(password)) {
                FlatAnimatedLafChange.showSnapshot();
                if (tk.getRole().equals("QL")) {
                    type = 0;
                    app.setContentPane(app.mainForm1);
                    app.mainForm1.applyComponentOrientation(app.getComponentOrientation());
                    setSelectedMenu(0, 0);
                    app.mainForm1.hideMenu();
                    SwingUtilities.updateComponentTreeUI(app.mainForm1);
                } else if (tk.getRole().equals("BH")) {
                    type = 1;
                    app.setContentPane(app.mainForm);
                    app.mainForm.applyComponentOrientation(app.getComponentOrientation());
                    setSelectedMenu(0, 0);
                    app.mainForm.hideMenu();
                    SwingUtilities.updateComponentTreeUI(app.mainForm);
                }
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            } else {
                NotifyToast.showErrorToast("Thông tin tài khoản không hợp lệ");
            }
        }
    }

    public static void logout() {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.loginForm);
        app.loginForm.applyComponentOrientation(app.getComponentOrientation());
        SwingUtilities.updateComponentTreeUI(app.loginForm);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void setSelectedMenu(int index, int subIndex) throws RemoteException, MalformedURLException, NotBoundException {
        if (type == 0) {
            app.mainForm1.setSelectedMenu(index, subIndex);
        } else if (type == 1) {
            app.mainForm.setSelectedMenu(index, subIndex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 719, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 521, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("Views.Components.Theme");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacLightLaf.setup();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                app = new Application();
                app.setVisible(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
