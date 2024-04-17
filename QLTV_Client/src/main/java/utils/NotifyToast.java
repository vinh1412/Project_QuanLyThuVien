package utils;

import raven.toast.Notifications;


public class NotifyToast {
    public static void showErrorToast(String title) {
        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_RIGHT, title);
    }
    public static void showSuccessToast(String title) {
        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_RIGHT, title);
    }
}
