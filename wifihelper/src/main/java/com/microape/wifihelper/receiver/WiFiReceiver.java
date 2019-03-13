package com.microape.wifihelper.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.microape.wifihelper.callback.WiFiAdapter;
import com.microape.wifihelper.utils.WiFiUtil;

import java.util.List;

/**
 * Created by pengle on 2018-09-13.
 * email:pengle609@163.com
 *
 * WiFi广播监听会发送多次
 * 1、屏蔽系统触发的扫描
 * 2、只监听指定规则的WiFi广播 <^DS\d{2}-\d{10,}$>
 * 3、对广播进行过滤
 *
 */

public class WiFiReceiver extends BroadcastReceiver {

    private WiFiAdapter wiFiAdapter;
    private WiFiUnit targetWifi;

    public WiFiReceiver(WiFiAdapter wiFiAdapter) {
        this.wiFiAdapter = wiFiAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WiFiAction.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            if (wifiState == WifiManager.WIFI_STATE_ENABLED){
                // TODO: 2018-09-13 wifi打开
                if (wiFiAdapter != null){
                    wiFiAdapter.wifiStateOpen();
                }
            }else if (wifiState == WifiManager.WIFI_STATE_DISABLED){
                // TODO: 2018-09-13 wifi关闭
                if (wiFiAdapter != null){
                    wiFiAdapter.wifiStateClose();
                }
            }
        }else if (WiFiAction.SCAN_RESULTS_START_ACTION.equals(action)) {
            // TODO: 2018-11-27 wifi 扫描开始
            if (wiFiAdapter != null){
                wiFiAdapter.wifiScanStarted();
            }
        }else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            // TODO: 2018-11-27 wifi 扫描结束
            if (WiFiStatus.newInstance().isScan()) {
                WifiManager wifiManager  = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                @SuppressLint("MissingPermission") List<ScanResult> scanResults = wifiManager.getScanResults();

                if(scanResults != null && scanResults.size() > 0) {
                    for (int i = 0; i < scanResults.size(); i++) {
                        ScanResult scanResult = scanResults.get(i);
                        if (WiFiUtil.newInstance().isMatcheName(scanResult.SSID)){
                            // TODO: 2018-10-08 留下
                            continue;
                        }
                        scanResults.remove(i);
                        i--;
                    }
                }
                // TODO: 2018-09-13 回调
                if (wiFiAdapter != null){
                    wiFiAdapter.wifiScanFound(scanResults);
                }
            }
        } else if (WiFiAction.NETWORK_STATE_CHANGED_ACTION.equals(action)){
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);    //WIFI连接状态
            if (null != networkInfo) {
                NetworkInfo.State state = networkInfo.getState();
                String wifiName = getWifiSSID(context);

                if (!WiFiUtil.newInstance().isMatcheName(wifiName)){
                    return;
                }else if (!targetWifi.matchSSID(wifiName)){
                    return;
                }

                if (state == NetworkInfo.State.CONNECTING){
                    WiFiAction.setWillConnect(true);
                }else if (state == NetworkInfo.State.CONNECTING){
                    WiFiAction.setWillDisConnect(true);
                }else if (WiFiAction.isWillConnect() && state == NetworkInfo.State.CONNECTED){
                    WiFiAction.setWillConnect(false);
                    if (wiFiAdapter != null ){
                        wiFiAdapter.wifiConnected();
                    }
                }else if (WiFiAction.isWillDisConnect() && state == NetworkInfo.State.DISCONNECTED){
                    WiFiAction.setWillDisConnect(false);
                    if (wiFiAdapter != null ){
                        wiFiAdapter.wifiDisConnected();
                    }
                }
            }
        } else if (WiFiAction.NETWORK_STATE_CONNECTED.equals(action)) {
            // TODO: 2018-11-27  自定义广播、手机已经连上指定WiFi
            String wifiName = intent.getStringExtra(WiFiUnit.TARGET_SSID);
            if (WiFiUtil.newInstance().isMatcheName(wifiName)) {
                if (wiFiAdapter != null ){
                    wiFiAdapter.wifiConnected();
                }
            }
        }else if (WiFiAction.ACTION_CONNECT_STARTED.equals(action)) {
            // TODO: 2018-11-27  自定义广播、开始连接指定WiFi
            String targetSSID = intent.getStringExtra(WiFiUnit.TARGET_SSID);
            String targetBSSID = intent.getStringExtra(WiFiUnit.TARGET_BSSID);
            targetWifi = new WiFiUnit(targetSSID, targetBSSID);
            // TODO: 2019/3/7 开启连接监听
            WiFiAction.setIsConnecting(true);
        }else if (WiFiAction.ACTION_CONNECT_TIMEOUT.equals(action)) {
            // TODO: 2018-11-27  自定义广播、超时时间到了
            if (!WiFiAction.isConnecting()){
                targetWifi = null;
                return;
            }
            String connectWifi = getWifiSSID(context);
            if (WiFiUtil.newInstance().isMatcheName(targetWifi.getTargetSSID()) && WiFiUtil.newInstance().isMatcheName(connectWifi)){
                if (connectWifi.equals(targetWifi.getTargetSSID())){
                    if (wiFiAdapter != null){
                        wiFiAdapter.wifiConnected();
                    }
                }else {
                    if (wiFiAdapter != null){
                        wiFiAdapter.wifiConnectFail();
                    }
                }
            }else {
                if (wiFiAdapter != null){
                    wiFiAdapter.wifiConnectFail();
                }
            }
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
            int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
            if (WifiManager.ERROR_AUTHENTICATING == error) {
                //TODO: 2018-09-14  密码错误,认证失败、待定？？？
                if (wiFiAdapter != null){
                    wiFiAdapter.wifiConnectFail();
                }
            }
        }else {
            // TODO: 2018-11-27  Log.i(">>>>>", "onReceive: action = " + action);
        }
    }


    /**
     * 获取SSID
     * @param context 上下文
     * @return  WIFI 的SSID
     */
    @SuppressLint("MissingPermission")
    public String getWifiSSID(Context context) {
        String ssid="unknown id";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O||Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            assert mWifiManager != null;
             WifiInfo info = mWifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT==Build.VERSION_CODES.O_MR1){
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            @SuppressLint("MissingPermission") NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo()!=null){
                    return networkInfo.getExtraInfo().replace("\"","");
                }
            }
        }
        return ssid;
    }

}
