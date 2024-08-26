package services;

import com.example.forhappywork.R;
import com.example.forhappywork.windowControl;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MainService extends Service {

    String serviceKey = "happy_work_servicerun";
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    @Override
    public void onCreate() {
        super.onCreate();
        String channelId = "HappyWork";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, "HappyWorkಥ_ಥ");
        }

        Notification notification = new Notification();
        try {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, channelId);
            notification = builder.setSmallIcon(R.drawable.chongwutubiao23).setTicker("快乐")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("进行时")
                    .setContentText("HappyWorkಥ_ಥ")
                    .setChannelId(channelId)
                    .build();
        } catch (Throwable e) {
            e.printStackTrace();
        }



        startForeground(R.layout.activity_main, notification);
        windowControl.getWindowControl().show(this);
        sharedPreferences = getSharedPreferences("happy_work_ಥ_ಥ", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(serviceKey, true);
        editor.apply();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("gps位置监听 进程一销毁"," fuck");
        editor = sharedPreferences.edit();
        editor.putBoolean(serviceKey, false);
        editor.apply();
        windowControl.getWindowControl().destory();
    }

    private LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void locListener() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"未开启定位权限，无法正常使用",Toast.LENGTH_LONG).show();
            return;
        }
        Log.e("1gps位置监听","开启");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 10,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.e("11gps位置监听",
                                "onLocationChanged" + location.toString() + "lat = " + location
                                        .getLatitude() + "lon = " + location.getLongitude());
//                        String[] strings = locString.split(",");
//                        if(getDistance(Double.valueOf(strings[0]), Double.valueOf(strings[1]),
//                                location.getLongitude(), location.getLatitude()) > 100) {
//                            // 离公司 100外 米
//                            if (isclose) {
//                                // 原来在公司范围之内 提醒位置变更 记得打卡下班
//                                addDDWindow(0);
//                                return;
//                            }
//                            isclose = false;
//                        } else {
//                            if (isclose) {
//                                // 原来在公司范围之外 提醒位置变更 记得打卡上班
//                                addDDWindow(1);
//                                return;
//                            }
//                            isclose = true;
//                        }
//                        testWifiState();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.e("1gps位置监听", "onStatusChanged" + provider);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.e("1gps位置监听", "onProviderEnabled" + provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.e("1gps位置监听", "onProviderDisabled" + provider);
                    }
                });
    }

}
