#WiFiHelper
WiFi socket

##如何使用（还未写完）

###Gradle
`compile 'com.microape.wifihelper:wifihelper:1.0.1'`

##Usage

> ###前提条件
> 1. 设备是否支持WiFi
> 2. 6.0手机是否允许定位权限（影响扫描）
> 3. 手机开启定位功能（影响扫描）

###init 
`WiFiHelper.newInstance().init(this);`

###设置监听
```
wiFiHelper.setOpenCallBack(this);
wiFiHelper.setSearchCallBack(this);
wiFiHelper.setConnCallBack(this);
```

###开启WiFi
`wiFiHelper.enable();`

###关闭WiFi
`wiFiHelper.disable();`

###扫描WiFi
`wiFiHelper.startScan();`

###连接指定WiFi
`wiFiHelper.connDevice(wifiResult, pwd);`

####根据ip和端口建立通讯
```



```