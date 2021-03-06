package com.example.forhappywork_;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.guib_annotation.Bind;
import com.example.guib_annotation.viewonclick;
import com.example.viewsethelp.bindhelp.ViewSetHelp;
import com.example.viewsethelp.bindhelp.apicenter.Api;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@Bind(177)
public class windowControl {

    static windowControl mInstance = null;
    private View mPopupContentView = null;
    private Context mContext = null;

    private WindowManager mWindowManager = null;
    WindowManager.LayoutParams params;

    public Boolean isShown = false;

    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;

    String ipKey = "happy_work_ipkey";
    String locKey = "happy_work_lockey";
    String lastNowWifiIp = "happy_work_LASTWIFIIP";
    String DDKey = "opendd";


    List<View> imageViews = new ArrayList<>();

    public static windowControl getWindowControl() {
        if (mInstance == null) {
            synchronized (windowControl.class) {
                mInstance = new windowControl();
            }
        }
        return mInstance;
    }

    @viewonclick(R.id.grouplayout)
    public void test() {
        Toast.makeText(mContext, "sss", Toast.LENGTH_LONG).show();
        ViewSetHelp.getApi().to(MainActivity.class.getSimpleName()).find(122661).set("","").call();
    }

    public void show(Context context) {
        if (isShown) {
            Log.i("service window", "return cause already shown");
            return;
        }
        sharedPreferences = context.getSharedPreferences("happy_work_???_???", Context.MODE_PRIVATE);

        // Log.i(TAG, "showPopupWindow");
        mPopupContentView = init(context);

        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;;
        params.flags = flags;
        // ??????????????????????????????????????????????????????
        params.format = PixelFormat.TRANSLUCENT;
        // params.format = PixelFormat.TRANSPARENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        mWindowManager.addView(mPopupContentView, params);
        Log.e("service window", "addview ??????");
        isShown = true;
        //locListener();// ????????????
        timer();
    }

    public void timer() {
        new Timer("???????????????").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e("gps???????????? ?????????", "ss");
                mPopupContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        testLocation();
                    }
                });
            }

        }, 3000, 5000);
    }

    ImageView icon1, icon2, icon3, mainIcon;
    EditText location, ip;
    LinearLayout ipLayout, locLayout;

    Button sureLoc, sureIp;

    public View init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_window, null);
        icon1 = view.findViewById(R.id.icon_1);
        icon2 = view.findViewById(R.id.icon_2);
        icon3 = view.findViewById(R.id.icon_3);
        location = view.findViewById(R.id.happy_location);
        ip = view.findViewById(R.id.happy_wifi_ip);
        ipLayout = view.findViewById(R.id.ip_layout);
        locLayout = view.findViewById(R.id.location_layout);
        mainIcon = view.findViewById(R.id.main_icon);
        sureLoc = view.findViewById(R.id.sure_loc);
        sureIp = view.findViewById(R.id.sure_ip);

        imageViews.add(mainIcon);
        imageViews.add(ipLayout);
        imageViews.add(locLayout);
        imageViews.add(icon3);

        ipLayout.setTag(false);
        locLayout.setTag(false);

        locString = sharedPreferences.getString(locKey, "22.527708,113.93762");
        iplisten = sharedPreferences.getString(ipKey, "Baidu_WiFi|BAIDU");
        nowWifiIp = sharedPreferences.getString(lastNowWifiIp,nowWifiIp);

        mainIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    closeEnterAnim(90);
                } else {
                    showEnterAnim(90);
                }



                try{
                    vibrator.cancel();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        ipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(Boolean) ipLayout.getTag()) {
                    ip.setVisibility(View.VISIBLE);
                    sureIp.setVisibility(View.VISIBLE);
                    ip.setText(sharedPreferences.getString(ipKey, ""));
                    ipLayout.setTag(true);
                } else {
                    ip.setVisibility(View.GONE);
                    sureIp.setVisibility(View.GONE);
                    ipLayout.setTag(false);
                }
            }
        });

        locLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(Boolean) locLayout.getTag()) {
                    location.setVisibility(View.VISIBLE);
                    sureLoc.setVisibility(View.VISIBLE);
                    location.setText(sharedPreferences.getString(locKey, ""));
                    locLayout.setTag(true);
                } else {
                    location.setVisibility(View.GONE);
                    sureLoc.setVisibility(View.GONE);
                    locLayout.setTag(false);
                }
            }
        });

        sureIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ??????ip??????
                sureIp.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ip.getText() != null && ip.getText().length() > 0) {
                            editor = sharedPreferences.edit();
                            editor.putString(ipKey, (ip.getText() != null && ip.getText().length() != 0) ? ip.getText().toString() : null);
                            editor.apply();
                            iplisten = (ip.getText() != null && ip.getText().length() != 0) ? ip.getText().toString() : iplisten;
                            Toast.makeText(mContext,"????????????!", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            Toast.makeText(mContext,"????????????!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });

            }
        });

        sureLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ??????????????????
                sureLoc.post(new Runnable() {
                    @Override
                    public void run() {
                        if (location.getText() != null) {
                            String[] strings = location.getText().toString().split(",");
                            if (strings.length != 2) {
                                Toast.makeText(mContext,"????????????!", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else {
                            Toast.makeText(mContext,"????????????!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        editor = sharedPreferences.edit();
                        editor.putString(locKey,
                                (location.getText() != null && location.getText().length() != 0) ? location.getText().toString() : null);
                        editor.apply();
                        locString = (location.getText() != null && location.getText().length() != 0) ? location.getText().toString() : locString;
                        Toast.makeText(mContext,"????????????!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return view;
    }

    private LocationManager locationManager;

    public void locListener() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "??????????????????????????????????????????", Toast.LENGTH_LONG).show();
            return;
        }
        Log.e("gps????????????", "??????");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        sleepOverTime();
                        Log.e("gps????????????",
                                "onLocationChanged" + location.toString() + "lat = " + location
                                        .getLatitude() + "lon = " + location.getLongitude());
                        String[] strings = locString.split(",");
                        if (strings != null && strings.length >= 2 && getDistance(Double.valueOf(strings[0]), Double.valueOf(strings[1]),
                                location.getLongitude(), location.getLatitude()) > 100) {
                            // ????????? 100??? ???
                            if (isclose) {
                                // ??????????????????????????? ?????????????????? ??????????????????
                                addDDWindow(0);
                                return;
                            }
                            isclose = false;
                        } else {
                            if (isclose) {
                                // ??????????????????????????? ?????????????????? ??????????????????
                                addDDWindow(1);
                                return;
                            }
                            isclose = true;
                        }
                        testWifiState();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.e("gps????????????", "onStatusChanged" + provider);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.e("gps????????????", "onProviderEnabled" + provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.e("gps????????????", "onProviderDisabled" + provider);
                    }
                });
    }

    /**
     * 0 - ???????????? 1 - ???????????? 2 - ???????????????wifi 3 - ????????????wifi
     * @param state
     */
    Vibrator vibrator;
    public void addDDWindow(int state) {
        vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        long[] patter = {300, 1000, 300, 1000, 300, 1000};

        final View view = LayoutInflater.from(mContext).inflate(R.layout.loc_change_window, null);
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickView) {
                // ???????????? ????????????
                mWindowManager.removeView(view);
                vibrator.cancel();
            }
        });
        view.findViewById(R.id.dd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(
                                "com.isoftstone.client.ipsa");
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(mContext,"????????????????????????????????????",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        TextView stateChangeTv = view.findViewById(R.id.state_change);
        switch (state) {
            case 0:
                stateChangeTv.setText("???????????????????????????????????????~");
                break;
            case 1:
                stateChangeTv.setText("???????????????????????????????????????~");
                break;
            case 2:
                stateChangeTv.setText("ip?????????????????????????????????~");
                break;
            case 3:
                stateChangeTv.setText("ip?????????????????????????????????~");
                break;
            case 4:
                stateChangeTv.setText("9???????????????????????????????????????????????????????????????????????????????????????????????????????????????loser?????????");
                view.findViewById(R.id.dd).setVisibility(View.GONE);
                break;
        }
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        mWindowManager.addView(view, params);
        vibrator.vibrate(patter, 0);
    }

    boolean isclose = false;

    String locString = "22.527708,113.93762";

    // ??????????????????
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static final double EARTH_RADIUS = 6378137.0;

    private void showEnterAnim(int dp) {
        ViewGroup.LayoutParams group = mPopupContentView.findViewById(R.id.grouplayout).getLayoutParams();
        group.width = ViewGroup.LayoutParams.MATCH_PARENT;
        group.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mPopupContentView.findViewById(R.id.grouplayout).setLayoutParams(group);

        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.flags = flags;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        mWindowManager.updateViewLayout(mPopupContentView,params);
        //for???????????????????????????????????????
        for (int i = 1; i < imageViews.size(); i++) {
            AnimatorSet set = new AnimatorSet();
            double x =
                    Math.cos(0.5 / (imageViews.size() - 2) * (i - 1) * Math.PI) * dip2px(mContext,
                            dp);
            double y =
                    Math.sin(0.5 / (imageViews.size() - 2) * (i - 1) * Math.PI) * dip2px(mContext,
                            dp);
            set.playTogether(
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationX", (float) (x * 0.25),
                            (float) x),
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationY", (float) (y * 0.25),
                            (float) y)
                    , ObjectAnimator.ofFloat(imageViews.get(i), "alpha", 0, 1).setDuration(2000)
            );
            set.setInterpolator(new BounceInterpolator());
            set.setDuration(500).setStartDelay(100 * i);
            set.start();
        }
        //???????????????????????????45??
        ObjectAnimator rotate =
                ObjectAnimator.ofFloat(imageViews.get(0), "rotation", 0, 45).setDuration(300);
        rotate.setInterpolator(new BounceInterpolator());
        rotate.start();

        //?????????????????????
        isOpen = true;
        Log.e("????????????", " ss");
    }

    private void closeEnterAnim(int dp) {
        //for???????????????????????????????????????
        for (int i = 1; i < imageViews.size(); i++) {
            AnimatorSet set = new AnimatorSet();
            double x =
                    Math.cos(0.5 / (imageViews.size() - 2) * (i - 1) * Math.PI) * dip2px(mContext,
                            dp);
            double y =
                    Math.sin(0.5 / (imageViews.size() - 2) * (i - 1) * Math.PI) * dip2px(mContext,
                            dp);
            set.playTogether(
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationX", (float) x,
                            (float) (x * 0.25)),
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationY", (float) y,
                            (float) (y * 0.25))
                    , ObjectAnimator.ofFloat(imageViews.get(i), "alpha", 1, 0).setDuration(1000)
            );
            set.setInterpolator(new BounceInterpolator());
            set.setDuration(500).setStartDelay(100 * i);
            set.start();
        }
        //???????????????????????????45??
        ObjectAnimator rotate =
                ObjectAnimator.ofFloat(imageViews.get(0), "rotation", 45, 0).setDuration(300);
        rotate.setInterpolator(new BounceInterpolator());
        rotate.start();

        mPopupContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                params.flags = flags;
                params.width = dip2px(mContext,30);
                params.height = dip2px(mContext,30);
                params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                mWindowManager.updateViewLayout(mPopupContentView,params);
            }
        },900);
        //?????????????????????
        isOpen = false;
        Log.e("????????????", " ss");
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    boolean isOpen = false;

    String iplisten = "Baidu_WiFi|BAIDU";
    String nowWifiIp = null;

    public void testWifiState() {
        String ip = getWifiName();
        if (ip == null) {
            if (nowWifiIp != null) {
                // ?????????????????????wifi??????
                addDDWindow(3);
            }
            nowWifiIp = null;
            editor = sharedPreferences.edit();
            editor.putString(lastNowWifiIp, nowWifiIp);
            editor.apply();
        } else {
            if (nowWifiIp == null && iplisten.contains(ip)) {
                // ???????????? ?????? wifi
                addDDWindow(2);
                nowWifiIp = ip;
                editor = sharedPreferences.edit();
                editor.putString(lastNowWifiIp, nowWifiIp);
                editor.apply();
            } else if (!iplisten.contains(ip)) {
                if (nowWifiIp != null) {
                    addDDWindow(3);
                }
                nowWifiIp = null;
                editor = sharedPreferences.edit();
                editor.putString(lastNowWifiIp, nowWifiIp);
                editor.apply();
            } else if (iplisten.contains(ip)) {
                nowWifiIp = ip;
            }
        }
    }

    // ???????????????
    public void sleepOverTime() {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            int nowHours = date.getHours();
            int nowMint = date.getMinutes();
            if (!sharedPreferences.getBoolean(DDKey, false)) {
                if (nowHours == 0 || nowHours == 24) {
                    editor = sharedPreferences.edit();
                    editor.putBoolean(DDKey, true);
                    editor.apply();
                }
                return;
            }
            if (nowHours == 9 &&  nowMint < 15 && (nowWifiIp == null || !iplisten.contains(nowWifiIp))) {
                // 9?????????????????????,???15??????
                startAlarm();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ???????????????????????????Uri
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(mContext,RingtoneManager.TYPE_RINGTONE);
    }


    MediaPlayer mMediaPlayer;
    boolean ddbegin = false;
    /**
     * ??????????????????
     * */
    private void startAlarm() {
        //??????????????????????????????????????????mMediaPlayer?????????
        if (ddbegin) {
            return;
        }
        addDDWindow(4);
        try {
            mMediaPlayer = MediaPlayer.create(mContext, getSystemDefultRingtoneUri());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mMediaPlayer == null) {//????????????????????????????????????????????????????????????????????????????????????
            // SoundPoolUtils.playCallWaitingAudio();//????????????????????????????????????????????????????????????
        } else {
            try {
                mMediaPlayer.setLooping(true);// ????????????
                try {
                    mMediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMediaPlayer.start();
                ddbegin = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void stopAlarm(int i) {
        if (i < 20) {
            return;
        }
        try {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        ddbegin = false;
        // SoundPoolUtils.stopCallWaitingAudio();
    }

    public void testLocation() {
        sleepOverTime();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "??????????????????????????????????????????", Toast.LENGTH_LONG).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            String[] strings = locString.split(",");
            if (strings != null && strings.length >= 2 && getDistance(Double.valueOf(strings[0]), Double.valueOf(strings[1]),
                    location.getLongitude(), location.getLatitude()) > 100) {
                // ????????? 100??? ???
                if (isclose) {
                    // ??????????????????????????? ?????????????????? ??????????????????
                    addDDWindow(0);
                    isclose = false;
                    return;
                }
                isclose = false;
            } else {

                if (!isclose) {
                    // ??????????????????????????? ?????????????????? ??????????????????
                    addDDWindow(1);
                    isclose = true;
                    return;
                }
                isclose = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        testWifiState();
    }

    public boolean isWifiEnabled() {
        if (mContext == null) {
            throw new NullPointerException("Global context is null");
        }
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }

    public String getWifiName() {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.getConnectionInfo().getSSID().replaceAll("\"","");
    }

//    public String getWifiIp() {
//        if (mContext == null) {
//            throw new NullPointerException("Global context is null");
//        }
//        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//        if (isWifiEnabled()) {
//            int ipAsInt = wifiMgr.getConnectionInfo().getIpAddress();
//            if (ipAsInt == 0) {
//                return null;
//            } else {
//                return intToInet(ipAsInt);
//            }
//        } else {
//            return null;
//        }
//    }
    //????????????int???????????????ip??????,?????????????????????????????????
    public String intToInet(int i)  {
        return (i & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
    }



}
