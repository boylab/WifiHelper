package com.microape.example;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.microape.wifihelper.R;
import com.microape.wifihelper.WiFiHelper;
import com.microape.wifihelper.callback.OnWifiConnCallBack;
import com.microape.wifihelper.callback.OnWifiOpenCallBack;
import com.microape.wifihelper.callback.OnWifiScanCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WiFiFuncActivity extends AppCompatActivity implements OnWifiOpenCallBack, OnWifiScanCallBack, OnWifiConnCallBack {

    @BindView(R.id.btn_OpenWiFi)
    Button btnOpenWiFi;
    @BindView(R.id.btn_CloseWiFi)
    Button btnCloseWiFi;
    @BindView(R.id.btn_ScanWiFi)
    Button btnScanWiFi;
    @BindView(R.id.lv_WiFiList)
    ListView lvWiFiList;
    private WiFiListAdapter wiFiListAdapter = null;
    private List<ScanResult> wifiList = new ArrayList<>();
    private WiFiHelper wiFiHelper = WiFiHelper.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);

        wiFiListAdapter = new WiFiListAdapter(this, wifiList);
        lvWiFiList.setAdapter(wiFiListAdapter);

        wiFiHelper.setOpenCallBack(this);
        wiFiHelper.setSearchCallBack(this);
        wiFiHelper.setConnCallBack(this);

    }

    @OnClick({R.id.btn_OpenWiFi, R.id.btn_CloseWiFi, R.id.btn_ScanWiFi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_OpenWiFi:
                wiFiHelper.enable();
                break;
            case R.id.btn_CloseWiFi:
                wiFiHelper.disable();
                break;
            case R.id.btn_ScanWiFi:
                wiFiHelper.startScan();
                break;
        }
    }

    @Override
    public void onWifiStateOpen() {
        Toast.makeText(this, "WiFi打开！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWifiStateClose() {
        Toast.makeText(this, "WiFi关闭！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWifiScanStarted() {
        Toast.makeText(this, "WiFi开始扫描！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWifiScanFound(List<ScanResult> scanResults) {
        if (scanResults != null ){
            wifiList.clear();
            wifiList.addAll(scanResults);
            wiFiListAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(this, "WiFi结束扫描，未扫到！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWifiConnectFail() {
        Toast.makeText(this, "WiFi断开连接！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWifiConnected() {
        Toast.makeText(this, "WiFi连接失败！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWifiDisConnected() {
        Toast.makeText(this, "WiFi连接失败！", Toast.LENGTH_SHORT).show();
    }
}
