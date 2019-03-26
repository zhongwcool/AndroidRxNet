# AndroidRxNet ([原始工程](https://github.com/zyj1609wz/AndroidRxNet))
一个用RxJava2实现的Android网络监听库，可以监听是否有网络连接，网络连接的类型，wifi是否连接，wifi信号强度

![](/image/pixel.png)

## 项目简介
这个项目总共有2个模块RxNet、RxNetDemo。
其中RxNet为网络监听的库，RxNetDemo分别为前面的demo示例。

部分参考开源项目 [https://github.com/pwittchen/ReactiveNetwork](https://github.com/pwittchen/ReactiveNetwork)并在源代码的基础上做了一些修改。

ReactiveNetwork是一个用RxJava实现的Android网络监听库，可以监听是否有网络连接，网络连接的类型，wifi是否连接

## Net主要功能:

  1、可以判断网络连接的类型
  
        UNKNOWN("unknown"),
        WIFI_CONNECTED("connected to WiFi network"),
        MOBILE_CONNECTED("connected to mobile network"),
        OFFLINE("offline"),
          
## RxNet主要功能：

  1、可以判断网络连接的类型
  
       UNKNOWN("unknown"),
       WIFI_CONNECTED("connected to WiFi network"),
       MOBILE_CONNECTED("connected to mobile network"),
       OFFLINE("offline"),
  2、当前环境是否有网络可用（是否接入互联网）
  
       true ,
       false 
  
  3、wifi 的状态
  
       WIFI_STATE_ENABLING( "wifi opening"),
       WIFI_STATE_ENABLED("wifi open"),
       WIFI_STATE_DISABLING("wifi closing"),
       WIFI_STATE_DISABLED("wifi closed"),
       WIFI_STATE_UNKNOWN( "wifi unknown" );

## 权限
* 使用Net的时候需要添加相应的权限
```
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
```

* 使用RxNet的时候需要添加相应的权限
```
<uses-permission android:name="android.permission.INTERNET"></uses-permission>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
<!--为了获取SSID -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```
     
## 需要注意的事项          
* RxNet需要依赖RxAndroid、RxJava

```
api 'io.reactivex.rxjava2:rxandroid:2.1.1'

api 'io.reactivex.rxjava2:rxjava:2.2.6'
```


* 相关的github地址

[https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)

[https://github.com/ReactiveX/RxAndroid]( https://github.com/ReactiveX/RxAndroid)
