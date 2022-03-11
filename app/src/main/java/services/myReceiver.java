package services;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class myReceiver extends BroadcastReceiver {
    String SCREEN_ON = "android.intent.action.SCREEN_ON";
    String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("gps位置监听 ppp","ppp-屏幕");
        if (!context.getSharedPreferences("happy_work_ಥ_ಥ", Context.MODE_PRIVATE).getBoolean("happy_work_servicerun",false)) {
            context.startService(new Intent(context, MainService.class));
        }
    }
}
