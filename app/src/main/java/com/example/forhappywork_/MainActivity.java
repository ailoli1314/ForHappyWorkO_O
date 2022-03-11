package com.example.forhappywork_;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import org.w3c.dom.Element;

import com.bumptech.glide.Glide;
import com.example.guib_annotation.Bind;
import com.example.guib_annotation.DoSomething_logic;
import com.example.guib_annotation.ViewSet;
import com.example.guib_annotation.api;
import com.example.guib_annotation.setContext;
import com.example.guib_annotation.viewonclick;
import com.example.viewsethelp.bindhelp.ViewSetHelp;
import com.example.viewsethelp.bindhelp.apicenter.Api;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import services.MainService;
import services.myReceiver;
@Bind(122661)
@setContext
public class MainActivity extends AppCompatActivity {

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

    @viewonclick(R.id.txt)
    private void test(Api api) {
        Toast.makeText(MainActivity.this, "sss", Toast.LENGTH_LONG).show();
        String byte_g = "111.111111,222.222222;333.333333,444.444444";
        byte[] ss = byte_g.getBytes();
        i++;
        windowControl.getWindowControl().stopAlarm(i);
        if (i > 20) {
            i = 0;
        }
    }

    @viewonclick(R.id.txt)
    private void test1() {
        Toast.makeText(MainActivity.this, "sdf", Toast.LENGTH_LONG).show();
    }

    @viewonclick(R.id.txt)
    public void test2() {
        Toast.makeText(MainActivity.this, "api", Toast.LENGTH_LONG).show();
    }

    @api(what = 122661)
    private void apitest(Api api) {

    }

    @api(what = 122662)
    public void apitest2(Api api) {

    }

    @ViewSet(id = R.id.txt, head = "iv",type = ViewSet.ViewType.IV)
    String dd = "test BIND iv";
    @ViewSet(id = R.id.txt, head = "tv",type = ViewSet.ViewType.TV)
    public String ss = "test BIND";

    public void hh(Element element) {
        try {
            Method ms = MainActivity.class.getDeclaredMethod("element.getSimpleName()");
            ms.setAccessible(true);
            ms.invoke(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewSetHelp.bind(this);
        sharedPreferences = getSharedPreferences("happy_work_ಥ_ಥ", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final TextView openDD = findViewById(R.id.naoz);
        openDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(DDKey, !sharedPreferences.getBoolean(DDKey, false));
                editor.apply();
                openDD.setSelected(sharedPreferences.getBoolean(DDKey, false));
            }
        });
        openDD.setSelected(sharedPreferences.getBoolean(DDKey, false));

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

        testPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void testPermission() {
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
            return;
        }
    }

}
