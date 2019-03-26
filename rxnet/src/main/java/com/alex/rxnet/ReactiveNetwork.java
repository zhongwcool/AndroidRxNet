package com.alex.rxnet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ${zyj} on 2016/7/15.
 */
public class ReactiveNetwork {
    private ConnectivityStatus status = ConnectivityStatus.UNKNOWN;
    private WifiManager wifiManager;

    /**
     * 判断链接的类型
     *
     * @param context
     * @return
     */
    public Observable<ConnectivityStatus> observeNetworkConnectivity(final Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        return Observable.create(new ObservableOnSubscribe<ConnectivityStatus>() {
            @Override
            public void subscribe(final ObservableEmitter<ConnectivityStatus> emitter) throws Exception {
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        final ConnectivityStatus newStatus = getConnectivityStatus(context);

                        // we need to perform check below,
                        // because after going off-line, onReceive() is called twice
                        if (newStatus != status) {
                            status = newStatus;
                            emitter.onNext(newStatus);
                        }
                    }
                };

                context.registerReceiver(receiver, filter);

                emitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("ALEX", "dispose()");
                        context.unregisterReceiver(receiver);
                    }

                    @Override
                    public boolean isDisposed() {
                        Log.e("ALEX", "isDisposed()");
                        return false;
                    }
                });
            }
        }).defaultIfEmpty(ConnectivityStatus.OFFLINE);
    }

    /**
     * 判断链接的类型
     *
     * @param context
     * @return
     */
    public Flowable<WifiInfo> observeWifiInfo(final Context context) {
        return Flowable.interval(1000, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .map(new Function<Long, WifiInfo>() {
                    @Override
                    public WifiInfo apply(Long aLong) throws Exception {
                        if (null == wifiManager) {
                            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        }
                        return wifiManager.getConnectionInfo();
                    }
                }).distinctUntilChanged();
    }

    /**
     * 测试网络可用性
     *
     * @return
     */
    public Observable<Boolean> observeInternetConnectivity() {
        return Observable.interval(2000, TimeUnit.MILLISECONDS, Schedulers.io())
                .map(new Function<Long, Boolean>() {
                    @Override
                    public Boolean apply(Long tick) {
                        try {
                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress("www.baidu.com", 80), 2000);
                            return socket.isConnected();
                        } catch (IOException e) {
                            return Boolean.FALSE;
                        }
                    }
                })
                .distinctUntilChanged();
    }


    /**
     * 监听wifi 按钮开关的
     *
     * @param context
     * @return
     */
    public Observable<ConnectivityStatus> observeWifiSwitch(final Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        return Observable.create(new ObservableOnSubscribe<ConnectivityStatus>() {
            @Override
            public void subscribe(final ObservableEmitter<ConnectivityStatus> emitter) throws Exception {
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                        switch (wifiState) {
                            case WifiManager.WIFI_STATE_DISABLED:
                                emitter.onNext(ConnectivityStatus.WIFI_STATE_DISABLED);
                                // Toast.makeText(context, "wifi 关闭了", Toast.LENGTH_SHORT).show();
                                break;
                            case WifiManager.WIFI_STATE_DISABLING:
                                emitter.onNext(ConnectivityStatus.WIFI_STATE_DISABLING);
                                //  Toast.makeText(context, "wifi 正在关闭", Toast.LENGTH_SHORT).show();
                                break;
                            case WifiManager.WIFI_STATE_ENABLED:
                                emitter.onNext(ConnectivityStatus.WIFI_STATE_ENABLED);
                                //  Toast.makeText(context, "wifi 打开了", Toast.LENGTH_SHORT).show();
                                break;
                            case WifiManager.WIFI_STATE_ENABLING:
                                emitter.onNext(ConnectivityStatus.WIFI_STATE_ENABLING);
                                //  Toast.makeText(context, "wifi 正在打开", Toast.LENGTH_SHORT).show();
                                break;
                            case WifiManager.WIFI_STATE_UNKNOWN:
                                emitter.onNext(ConnectivityStatus.WIFI_STATE_UNKNOWN);
                                break;
                        }
                    }
                };

                context.registerReceiver(receiver, filter);

                emitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        Log.e("ALEX", "dispose()");
                        context.unregisterReceiver(receiver);
                    }

                    @Override
                    public boolean isDisposed() {
                        Log.e("ALEX", "isDisposed()");
                        return false;
                    }
                });
            }
        });

    }

    /**
     * Gets current network connectivity status
     *
     * @param context Application Context is recommended here
     * @return ConnectivityStatus, which can be WIFI_CONNECTED, MOBILE_CONNECTED or OFFLINE
     */
    private ConnectivityStatus getConnectivityStatus(final Context context) {
        final String service = Context.CONNECTIVITY_SERVICE;
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return ConnectivityStatus.OFFLINE;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return ConnectivityStatus.WIFI_CONNECTED;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return ConnectivityStatus.MOBILE_CONNECTED;
        }

        return ConnectivityStatus.OFFLINE;
    }
}
