package com.microape.wifihelper.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by pengle on 2018-11-26.
 * email:pengle609@163.com
 */
public class WiFiUtil {

    private final String UNKNOWN_SSID = "<unknown ssid>";
    private Context mContext;
    private WifiManager mWifiManager;

    private WiFiUtil() {

    }

    private static class SingletonInstance {
        private static final WiFiUtil INSTANCE = new WiFiUtil();
    }

    public static WiFiUtil newInstance() {

        return WiFiUtil.SingletonInstance.INSTANCE;
    }

    public WiFiUtil initUtil(Context context, WifiManager mWifiManager) {
        this.mContext = context.getApplicationContext();
        this.mWifiManager = mWifiManager;
        return this;
    }

    /**
     * 连接WiFi
     * @param pwd
     * @return
     * @throws InterruptedException
     */
    public boolean connectWifi(ScanResult result, final String pwd) throws InterruptedException {
        boolean connResult = false;
        String security = Wifi.ConfigSec.getScanResultSecurity(result);
        WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, result, security);
        if(config == null) {
            //连接新WiFi
            int numOpenNetworksKept =  Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
            String scanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(result);
            boolean isOpenNetwork = Wifi.ConfigSec.isOpenNetwork(scanResultSecurity);
            connResult = Wifi.connectToNewNetwork(mContext, mWifiManager, result, (isOpenNetwork ? null : pwd), numOpenNetworksKept);
            return connResult;
        } else {
            final boolean isCurrentNetwork_Status = (config.status == WifiConfiguration.Status.CURRENT);
            final WifiInfo info = mWifiManager.getConnectionInfo();
            final boolean isCurrentNetwork_WifiInfo = info != null && TextUtils.equals(info.getSSID(), result.SSID) && TextUtils.equals(info.getBSSID(), result.BSSID);
            if(!isCurrentNetwork_Status && !isCurrentNetwork_WifiInfo) {
                //连接已保存的WiFi
                String scanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(result);
                final WifiConfiguration wcg = Wifi.getWifiConfiguration(mWifiManager, result, scanResultSecurity);
                if(wcg != null) {
                    connResult = Wifi.connectToConfiguredNetwork(mContext, mWifiManager, wcg, false);
                }
                return connResult;
            } else {
                //点击的是当前已连接的WiFi
                return true;
            }
        }
    }

    /**
     * 清除当前连接的WiFi网络
     * SSID().replace("\"", "")
     */
    public void clearConnectWifi() {
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
        if (isMatcheName(connectionInfo.getSSID())){
            // TODO: 2018-11-30 当前连接的是DS系列WiFi
            connectionInfo.getNetworkId();
            mWifiManager.removeNetwork(connectionInfo.getNetworkId());
            mWifiManager.saveConfiguration();
        }

        // TODO: 2018-11-30 清除多余的WiFi
        List<WifiConfiguration> wifiConfigurations = mWifiManager.getConfiguredNetworks();
        if(wifiConfigurations != null && wifiConfigurations.size() > 0) {
            for(WifiConfiguration wifiConfiguration : wifiConfigurations) {
                if(isMatcheName(wifiConfiguration.SSID)) {
                    mWifiManager.removeNetwork(wifiConfiguration.networkId);
                    mWifiManager.saveConfiguration();
                }
            }
        }
    }

    public boolean isMatcheName(String wifiName){
        if (TextUtils.isEmpty(wifiName) || UNKNOWN_SSID.equals(wifiName)){
            return false;
        }

        if (wifiName.matches("^DS\\d{2}-\\d{10,}$")){
            return true;
        }else if (wifiName.matches("^\"DS\\d{2}-\\d{10,}\"$")){
            return true;
        }

        return false;
    }

}
