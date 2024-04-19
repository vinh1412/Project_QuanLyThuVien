/*
 * @ {#} IconUtils.java   1.0     19/04/2024
 *
 * Copyright (c) 2024 IUH. All rights reserved.
 */

package utils;

import javax.swing.*;
import java.awt.*;

/*
 * @description:
 * @author: Tran Hien Vinh
 * @date:   19/04/2024
 * @version:    1.0
 */
public class IconUtils {
    public static Icon createScaledIcon(String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    public static Icon addIcon() {
        return createScaledIcon("src/main/java/img/svgIcon/add.png", 20, 20);
    }
    public static Icon updateIcon() {
        return createScaledIcon("src/main/java/img/svgIcon/update.png", 20, 20);
    }
    public static Icon filterIcon() {
        return createScaledIcon("src/main/java/img/svgIcon/filter.png", 20, 20);
    }
    public static Icon refreshIcon() {
        return createScaledIcon("src/main/java/img/svgIcon/refresh.png", 20, 20);
    }
    public static Icon queueIcon() {
        return createScaledIcon("src/main/java/img/svgIcon/queue.png", 20, 20);
    }
}
