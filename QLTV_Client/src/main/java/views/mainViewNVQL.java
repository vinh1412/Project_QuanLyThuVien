package views;

import entity.TaiKhoan;
import views.components.Menu;
import views.components.MenuAction;
import com.formdev.flatlaf.util.UIScale;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class mainViewNVQL extends JLayeredPane {
    private TaiKhoan tk;
    public mainViewNVQL() {
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        menu = new Menu(true);
        panelBody = new JPanel(new BorderLayout());
        initMenuEvent();
        add(menu);
        add(panelBody);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
    }

    private void initMenuEvent() {
        menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
            // Application.mainForm.showForm(new DefaultForm("Form : " + index + " " + subIndex));
            switch (index) {
                case 0 -> Application.showForm(new panel_TrangChu());
                case 1 -> Application.showForm(new panel_QuanLySanPham());
                case 2 -> Application.showForm(new panel_QuanLyNhanVien());
                case 3 -> Application.showForm(new panel_QuanLyTacGia());
                case 4 -> {
                    try {
                        Application.showForm(new panel_QuanLyHoaDon());
                    } catch (SQLException | RemoteException ex) {
                        Logger.getLogger(mainViewNVQL.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                case 5 -> Application.showForm(new panel_QuanLyNhaCungCap());
                case 6 -> Application.showForm(new panel_QuanLyNhaXuatBan());
                case 7 -> {
                    try {
                        Application.showForm(new panel_ThongKeNVQL());
                    } catch (SQLException | RemoteException ex) {
                        Logger.getLogger(mainViewNVQL.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case 8 -> {
                try {
                    Application.showForm(new panel_TroGiup());
                } catch (URISyntaxException ex) {
                    Logger.getLogger(mainViewNVQL.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
                case 9 -> new JDialogDoiMatKhau(null, true).setVisible(true);
                case 10 -> Application.logout();
                default -> action.cancel();
            }
        });
    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    public void setSelectedMenu(int index, int subIndex) throws RemoteException, MalformedURLException, NotBoundException {
        menu.setSelectedMenu(index, subIndex);
    }

    private Menu menu;
    private JPanel panelBody;

    private class MainFormLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                menu.setBounds(menuX, y, menuWidth, height);
                int menuButtonWidth = 0;
                int menuButtonHeight = 0;
                int menubX;
                if (ltr) {
                    menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
                    menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }
}
