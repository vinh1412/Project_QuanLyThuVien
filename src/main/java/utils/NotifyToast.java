/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import raven.toast.Notifications;

/**
 *
 * @author ngoct
 */
public class NotifyToast {
    public static void showErrorToast(String title) {
        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_RIGHT, title);
    }
    public static void showSuccessToast(String title) {
        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_RIGHT, title);
    }
}
