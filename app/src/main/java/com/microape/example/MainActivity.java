package com.microape.example;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_SuportWiFi, btn_Permissions, btn_OpenGPS, btn_Function;
    CheckBox cb_SuportWiFi, cb_Permissions, cb_OpenGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_SuportWiFi = findViewById(R.id.btn_SuportWiFi);
        btn_Permissions = findViewById(R.id.btn_Permissions);
        btn_OpenGPS = findViewById(R.id.btn_OpenGPS);
        btn_Function = findViewById(R.id.btn_Function);
        cb_SuportWiFi = findViewById(R.id.cb_SuportWiFi);
        cb_Permissions = findViewById(R.id.cb_Permissions);
        cb_OpenGPS = findViewById(R.id.cb_OpenGPS);
        btn_SuportWiFi.setOnClickListener(this);
        btn_Permissions.setOnClickListener(this);
        btn_OpenGPS.setOnClickListener(this);
        btn_Function.setOnClickListener(this);

        boolean isSuportWiFi = getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
        btn_Permissions.setEnabled(isSuportWiFi);
        cb_SuportWiFi.setChecked(isSuportWiFi);
        btn_Permissions.setClickable(isSuportWiFi);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Permissions:
                // TODO: 2019/3/12 申请权限(编译后自动生成)
                MainActivityPermissionsDispatcher.needsLocationWithCheck(this);
                break;
            case R.id.btn_OpenGPS:
                openGPS();
                break;
            case R.id.btn_Function:
                funcTest();
                break;
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void needsLocation() {
        cb_Permissions.setChecked(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showLocationRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.show_location_msg)
                .setPositiveButton(R.string.show_location_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.show_location_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationDenied() {
        cb_Permissions.setChecked(false);
        Toast.makeText(this, R.string.show_location_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationNeverAsk() {
        cb_Permissions.setChecked(false);
        Toast.makeText(this, R.string.show_location_neverask, Toast.LENGTH_SHORT).show();
    }

    private void funcTest() {
        if (!cb_SuportWiFi.isChecked()) {
            Toast.makeText(this, R.string.location_non_suport, Toast.LENGTH_SHORT).show();
        } else if (!cb_Permissions.isChecked()) {
            Toast.makeText(this, R.string.location_non_permission, Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, WiFiFuncActivity.class);
        startActivity(intent);
    }

    private void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    public void openGPS(){
        if (isOPen(MainActivity.this)){
            WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                cb_OpenGPS.setChecked(true);
            } else {
                Toast.makeText(this, "请先开启手机WIFI！", Toast.LENGTH_SHORT).show();
            }
        }else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("温馨提示!")
                    .setMessage("打开手机GPS才能扫描Wifi，是否开启？")
                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }


}
