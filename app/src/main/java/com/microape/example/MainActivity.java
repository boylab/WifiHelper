package com.microape.example;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

    Button btnSuportWiFi;
    Button btnPermissions;
    Button btnFunction;
    CheckBox cbSuportWiFi, cbPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSuportWiFi = findViewById(R.id.btn_SuportWiFi);
        btnPermissions = findViewById(R.id.btn_Permissions);
        btnFunction = findViewById(R.id.btn_Function);
        cbSuportWiFi = findViewById(R.id.cb_SuportWiFi);
        cbPermissions = findViewById(R.id.cb_Permissions);
        btnSuportWiFi.setOnClickListener(this);
        btnPermissions.setOnClickListener(this);
        btnFunction.setOnClickListener(this);


        boolean isSuportWiFi = getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
        btnPermissions.setEnabled(isSuportWiFi);
        cbSuportWiFi.setChecked(isSuportWiFi);
        btnPermissions.setClickable(isSuportWiFi);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Permissions:
                // TODO: 2019/3/12 申请权限(编译后自动生成)
                MainActivityPermissionsDispatcher.needsLocationWithCheck(this);
                break;
            case R.id.btn_Function:
                funcTest();
                break;
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void needsLocation() {
        cbPermissions.setChecked(true);
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
        cbPermissions.setChecked(false);
        Toast.makeText(this, R.string.show_location_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onLocationNeverAsk() {
        cbPermissions.setChecked(false);
        Toast.makeText(this, R.string.show_location_neverask, Toast.LENGTH_SHORT).show();
    }

    private void funcTest() {
        if (!cbSuportWiFi.isChecked()) {
            Toast.makeText(this, R.string.location_non_suport, Toast.LENGTH_SHORT).show();
        } else if (!cbPermissions.isChecked()) {
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


}
