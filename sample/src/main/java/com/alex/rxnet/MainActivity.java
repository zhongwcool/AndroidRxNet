package com.alex.rxnet;

import android.Manifest;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    private static final int RC_CAMERA_AND_LOCATION = 0x100;
    private final String[] basicPerms = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private TextView tvConnectivityStatus;
    private TextView tvInternetStatus;
    private TextView wifiSwitchStatus;
    private TextView wifiInfoStatus;

    private ReactiveNetwork reactiveNetwork;
    private CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvConnectivityStatus = findViewById(R.id.tv1);
        tvInternetStatus = findViewById(R.id.tv2);
        wifiSwitchStatus = findViewById(R.id.tv3);
        wifiInfoStatus = findViewById(R.id.tv4);

        reactiveNetwork = new ReactiveNetwork();
        if (null == disposables) {
            disposables = new CompositeDisposable();
        }

        if (checkPermsBasic()) sendTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != disposables) disposables.dispose();
    }

    private void sendTask() {
        //监听网络连接类型的 （数据流量 、wifi 、断线）
        Disposable nc = reactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConnectivityStatus>() {
                    @Override
                    public void accept(final ConnectivityStatus status) throws Exception {

                        Log.d(TAG, status.toString());
                        tvConnectivityStatus.setText("网络连接状态： " + status.description);
                    }
                });
        disposables.add(nc);

        //监听wifi强度
        Disposable wi = reactiveNetwork.observeWifiInfo(getApplicationContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WifiInfo>() {
                    @Override
                    public void accept(WifiInfo info) {
                        int strength = 100 + info.getRssi();
                        wifiInfoStatus.setText(info.getSSID() + " " + strength);
                    }
                });
        disposables.add(wi);

        //监听是否链接互联网的 （ 是 ， 否）
        Disposable ic = reactiveNetwork.observeInternetConnectivity()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        tvInternetStatus.setText("是否有可用的网络： " + aBoolean.toString());
                    }
                });
        disposables.add(ic);

        //监听wifi 开关状态的（ wifi 正在打开 、 wifi 打开 、wifi 正在关闭、 wifi 关闭）
        Disposable ws = reactiveNetwork.observeWifiSwitch(this)
                .subscribe(new Consumer<ConnectivityStatus>() {
                    @Override
                    public void accept(ConnectivityStatus connectivityStatus) {
                        wifiSwitchStatus.setText("wifi 是否打开： " + connectivityStatus.description);
                    }
                });
        disposables.add(ws);
    }

    //==================== 权限处理 start ===========================================================
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //do something
        if (EasyPermissions.hasPermissions(this, basicPerms)) {
            //Already have permission, do the thing
            sendTask();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("请求权限")
                    .setRationale("应用依赖以下权限才能正常运行，去\"设置-权限\"中打开?")
                    .setNegativeButton("不用了")
                    .setPositiveButton(R.string.go_to_settings)
                    .build()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            checkPermsBasic();
        }
    }

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private boolean checkPermsBasic() {
        if (EasyPermissions.hasPermissions(this, basicPerms)) {
            //Already have permission, do the thing
            return true;
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_rationale),
                    RC_CAMERA_AND_LOCATION,
                    basicPerms
            );
            return false;
        }
    }
    //==================== 权限处理 end =============================================================
}
