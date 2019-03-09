package com.microape.wifihelper.receiver;

import android.text.TextUtils;

public class WiFiUnit {

    /**
     * wifi名称
     */
    public static final String TARGET_SSID = "TARGET_SSID";

    /**
     * wifi地址
     */
    public static final String TARGET_BSSID = "TARGET_BSSID";

    private String targetSSID, targetBSSID;

    public WiFiUnit(String targetSSID, String targetBSSID) {
        this.targetSSID = targetSSID;
        this.targetBSSID = targetBSSID;
    }

    public String getTargetSSID() {
        return targetSSID;
    }

    public void setTargetSSID(String targetSSID) {
        this.targetSSID = targetSSID;
    }

    public String getTargetBSSID() {
        return targetBSSID;
    }

    public void setTargetBSSID(String targetBSSID) {
        this.targetBSSID = targetBSSID;
    }

    public boolean matchSSID(String ssid) {
        if (!TextUtils.isEmpty(this.targetSSID)){
            if (this.targetSSID.equals(ssid)){
                return true;
            }
        }
        return false;
    }

    public boolean matchBSSID(String bssid) {
        if (!TextUtils.isEmpty(this.targetBSSID)){
            if (this.targetBSSID.equals(bssid)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "WiFiUnit{" +
                "targetSSID='" + targetSSID + '\'' +
                ", targetBSSID='" + targetBSSID + '\'' +
                '}';
    }
}
