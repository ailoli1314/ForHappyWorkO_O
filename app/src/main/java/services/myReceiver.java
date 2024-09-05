package services;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class myReceiver extends DeviceAdminReceiver {
    String SCREEN_ON = "android.intent.action.SCREEN_ON";
    String SCREEN_OFF = "android.intent.action.SCREEN_OFF";

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "设备管理员权限已启用", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context, "设备管理员权限已启用", Toast.LENGTH_SHORT).show();
        return super.onDisableRequested(context, intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("gps位置监听 ppp","ppp-屏幕");
        if (!context.getSharedPreferences("happy_work_ಥ_ಥ", Context.MODE_PRIVATE).getBoolean("happy_work_servicerun",false)) {
            context.startService(new Intent(context, MainService.class));
        }
    }
}
