/*
 * @ {#} RMIServiceURL.java   1.0     17/04/2024
 *
 * Copyright (c) 2024 IUH. All rights reserved.
 */

package utils;

/*
 * @description:
 * @author: Tran Hien Vinh
 * @date:   17/04/2024
 * @version:    1.0
 */
public class RMIServiceURL {
    private static final String DEFAULT_HOST = "192.168.73.62";
    private static final int DEFAULT_PORT = 5151;
    private static final String DEFAULT_URL = "rmi://" + DEFAULT_HOST + ":" + DEFAULT_PORT + "/";

    private RMIServiceURL() {
    }
    public static String getDefaultURL() {
        return DEFAULT_URL;
    }

    public static String getURL(String host, int port) {
        return "rmi://" + host + ":" + port + "/";
    }
}
