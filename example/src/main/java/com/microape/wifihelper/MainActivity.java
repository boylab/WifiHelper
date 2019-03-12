package com.microape.wifihelper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_SuportWiFi)
    Button btnSuportWiFi;
    @BindView(R.id.btn_Permissions)
    Button btnPermissions;
    @BindView(R.id.btn_Function)
    Button btnFunction;

    private boolean isSuportWiFi = false, isPermWiFi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_SuportWiFi, R.id.btn_Permissions, R.id.btn_Function})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_SuportWiFi:
                suportWiFi();
                break;
            case R.id.btn_Permissions:
                // TODO: 2019/3/12 申请权限
                MainActivityPermissionsDispatcher.needsLocationWithCheck(this);
                break;
            case R.id.btn_Function:
                funcTest();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void suportWiFi() {
        isSuportWiFi = getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);

        btnPermissions.setEnabled(isSuportWiFi);
        btnPermissions.setClickable(isSuportWiFi);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void needsLocation() {
        isPermWiFi = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showLocationRationale(final PermissionRequest request) {
        Log.i(">>>>>", "showLocationRationale: >>>>>");
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("挑战需要定位权限，应用将要申请定位权限")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("不给", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationDenied() {
        Log.i(">>>>>", "onLocationDenied: >>>>>");
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationNeverAsk() {
        Log.i(">>>>>", "onLocationNeverAsk: >>>>>");
    }


    private void funcTest() {
        if (!isSuportWiFi){
            Toast.makeText(this, "设备不支持Wifi！", Toast.LENGTH_SHORT).show();
        }else if (!isPermWiFi){

        }
    }

}
