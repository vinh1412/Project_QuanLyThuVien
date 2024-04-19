package utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Color;
import javax.swing.Icon;


public class getCommonIcons {
     public static Icon addIcon() {
         Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("/src/main/resources/META-INF/img/svgIcon/add-icon.svg");
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#969696"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
     }
     public static Icon updateIcon() {
         Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("src/main/java/Img/svgIcon/update.svg");
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#969696"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
     }
     public static Icon refreshIcon() {
         Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("src/main/java/Img/svgIcon/refresh.svg");
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#969696"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
     }
     public static Icon filterIcon() {
         Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("src/main/java/Img/svgIcon/filter.svg");
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#969696"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
     }
     public static Icon queueIcon() {
         Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("src/main/java/img/svgIcon/queue.svg");
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#969696"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
     }
}
