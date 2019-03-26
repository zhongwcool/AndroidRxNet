package com.alex.rxnet;

import io.reactivex.functions.Function;

/**
 * Created by ${zyj} on 2016/7/15.
 */
public enum ConnectivityStatus {

    UNKNOWN("未知"),
    WIFI_CONNECTED("WiFi"),
    MOBILE_CONNECTED("移动网络"),
    OFFLINE("无网络"),

    WIFI_STATE_ENABLING("WiFi opening"),
    WIFI_STATE_ENABLED("WiFi open"),
    WIFI_STATE_DISABLING("WiFi closing"),
    WIFI_STATE_DISABLED("WiFi closed"),
    WIFI_STATE_UNKNOWN("WiFi unknown");

    public final String description;

    ConnectivityStatus(final String description) {
        this.description = description;
    }

    /**
     * Creates a function, which checks
     * if single connectivity status or many statuses
     * are equal to current status. It can be used inside filter(...)
     * method from RxJava
     *
     * @param statuses many connectivity statuses or single status
     * @return Func1<ConnectivityStatus   ,       Boolean> from RxJava
     */
    public static Function<ConnectivityStatus, Boolean> isEqualTo(final ConnectivityStatus... statuses) {
        return new Function<ConnectivityStatus, Boolean>() {
            @Override
            public Boolean apply(ConnectivityStatus connectivityStatus) {
                boolean statuesAreEqual = false;

                for (ConnectivityStatus singleStatus : statuses) {
                    statuesAreEqual = singleStatus == connectivityStatus;
                }

                return statuesAreEqual;
            }
        };
    }

    /**
     * Creates a function, which checks
     * if single connectivity status or many statuses
     * are not equal to current status. It can be used inside filter(...)
     * method from RxJava
     *
     * @param statuses many connectivity statuses or single status
     * @return Func1<ConnectivityStatus   ,       Boolean> from RxJava
     */
    public static Function<ConnectivityStatus, Boolean> isNotEqualTo(
            final ConnectivityStatus... statuses) {
        return new Function<ConnectivityStatus, Boolean>() {
            @Override
            public Boolean apply(ConnectivityStatus connectivityStatus) {
                boolean statuesAreNotEqual = false;

                for (ConnectivityStatus singleStatus : statuses) {
                    statuesAreNotEqual = singleStatus != connectivityStatus;
                }

                return statuesAreNotEqual;
            }
        };
    }

    @Override
    public String toString() {
        return "ConnectivityStatus{" + "description='" + description + '\'' + '}';
    }

}
