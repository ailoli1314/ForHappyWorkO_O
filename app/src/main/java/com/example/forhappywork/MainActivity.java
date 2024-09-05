package com.example.forhappywork;


import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

import com.example.forhappywork.baseadapter.BaseAdapter;
import com.example.forhappywork.baseviewset.BaseActivity;
import com.example.forhappywork.baseviewset.BaseAdapterViewSet;
import com.example.forhappywork.dataviewset.AdapterData;
import com.example.guib_annotation.Bind;
import com.example.guib_annotation.ViewSet;
import com.example.guib_annotation.api;
import com.example.guib_annotation.setContext;
import com.example.guib_annotation.viewonclick;
import com.example.viewsethelp.bindhelp.ViewSetHelp;
import com.example.viewsethelp.bindhelp.apicenter.Api;
import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import services.MainService;
import services.myReceiver;

@Bind(122661)
@setContext
public class MainActivity extends BaseActivity {

    int i = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String DDKey = "opendd";


    private static URI getIP(URI uri) {
        URI effectiveURI = null;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }

        return effectiveURI;
    }

    @viewonclick(id = R.id.txt)
    private void test() {
        changeTest();
        value_change(ss);
        Toast.makeText(MainActivity.this, "sss", Toast.LENGTH_LONG).show();
        String byte_g = "111.111111,222.222222;333.333333,444.444444";
        byte[] ss = byte_g.getBytes();
        i++;
        windowControl.getWindowControl().stopAlarm(i);
        if (i > 20) {
            i = 0;
        }
    }

    int index = 0;
    @ViewSet(id = R.id.naoz, head = "tv",type = ViewSet.ViewType.TV)
    private String naoz = "闹钟⏰";
    @ViewSet(id = R.id.txt, head = "iv",type = ViewSet.ViewType.IV)
    public String dd = "test BIND iv";
    @ViewSet(id = R.id.txt, head = "tv ", foot = " foot",type = ViewSet.ViewType.TV)
    public String ss = "800";

    @viewonclick(id = R.id.naoz)
    public void test4() {
        changeTest();
        value_change(naoz);
        initsharedPreferences();
        final TextView openDD = findViewById(R.id.naoz);
        editor.putBoolean(DDKey, !sharedPreferences.getBoolean(DDKey, false));
        editor.apply();
        openDD.setSelected(sharedPreferences.getBoolean(DDKey, false));
    }

    public void changeTest() {
        index++;
        naoz = index + "";
        ss = index + "";
    }

    @api(what = 122669)
    private void apitest(Api api) {
        rootView().post(new Runnable() {
            @Override
            public void run() {
                initAppList();
            }
        });
    }

    @api(what = 122662)
    public void apitest2(Api api) {

    }

    public void hh(Element element) {
        try {
            Method ms = MainActivity.class.getDeclaredMethod("element.getSimpleName()");
            ms.setAccessible(true);
            ms.invoke(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initsharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("happy_work_ಥ_ಥ", Context.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = sharedPreferences.edit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewSetHelp.bind(this);
        final TextView openDD = findViewById(R.id.naoz);
        initsharedPreferences();
        openDD.setSelected(sharedPreferences.getBoolean(DDKey, false));
        testPermission();
        initAppList();
        initBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(packageRemovedReceiver);
        destory();
    }

    public BaseAdapter adapter = new BaseAdapter(null);

    public void initAppList() {
        // 获取 PackageManager 实例
        PackageManager packageManager = getPackageManager();

        // 获取已安装应用的列表
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        ArrayList<BaseAdapterViewSet> dataList = new ArrayList<>();
        // 遍历应用列表并输出应用信息
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if (isUserApp(appInfo)) {
                String appName = packageManager.getApplicationLabel(appInfo).toString();
                String packageName = appInfo.packageName;
                Drawable appIcon = packageManager.getApplicationIcon(appInfo);
                dataList.add(new AdapterData(appIcon, appName, packageName));
            }
        }
        if (adapter != null) {
            adapter.destoryItems();
        }
        adapter = new BaseAdapter(dataList);
        RecyclerView recyclerView = findViewById(R.id.recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(packageRemovedReceiver, filter);
    }

    private final BroadcastReceiver packageRemovedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                ViewSetHelp.getApi().find(122669).to(MainActivity.class).call();
                // 处理应用卸载逻辑
                Toast.makeText(context, "应用刷新～", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean isUserApp(ApplicationInfo appInfo) {
        // 通过 flags 判断是否为用户应用
        boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        boolean isUpdatedSystemApp = (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        return !isSystemApp && !isUpdatedSystemApp;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void testPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), 12266);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(MainActivity.this, MainService.class));
            }
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"未开启定位权限",Toast.LENGTH_SHORT).show();
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(localIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // 获取最近一个小时内的应用使用记录
            List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 3600, time);

            if (appList != null && appList.size() > 0) {
                for (UsageStats usageStats : appList) {
                    Log.e("UsageStats", "Package: " + usageStats.getPackageName() + " Last time used: " + usageStats.getLastTimeUsed());
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
                    122661);
        }

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, myReceiver.class);
        if (!dpm.isAdminActive(adminComponent)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "请启用设备管理员权限以便更好地管理设备。");
            startActivity(intent);
        }

    }
}
