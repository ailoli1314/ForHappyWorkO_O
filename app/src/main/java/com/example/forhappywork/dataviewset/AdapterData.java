package com.example.forhappywork.dataviewset;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forhappywork.R;
import com.example.forhappywork.baseviewset.BaseAdapterViewSet;
import com.example.guib_annotation.DoSomething;
import com.example.guib_annotation.ViewSet;
import com.example.guib_annotation.viewonclick;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by v_zhangguibin on 2024/8/22.
 */
public class AdapterData extends BaseAdapterViewSet {

    @ViewSet(id = R.id.title)
    public String mTitle;
    @ViewSet(id = R.id.head, type = ViewSet.ViewType.IV)
    public Drawable mHead;

    String mPackageName;

    public AdapterData(Drawable head, String title, String packageName) {
        mTitle = title;
        mHead = head;
        mPackageName = packageName;
    }

    @DoSomething
    public void init1() {
        if (mTitle.contains("Work")) {
            mTitle = "牛马自律";
        }
    }

    @viewonclick(id = R.id.item)
    public void itemClick() {
        rootView().findViewById(R.id.more).setVisibility(View.VISIBLE);
    }

    private Timer timer;

    @viewonclick(id = R.id.set_timer)
    public void setTimer() {
        EditText time = rootView().findViewById(R.id.time_edit);
        if (TextUtils.isEmpty(time.getText())) {
            Toast.makeText(rootView().getContext(), "未设置时间", Toast.LENGTH_SHORT).show();
            return;
        }
        int times;
        try {
            times = Integer.parseInt(time.getText().toString());
        } catch (Exception e) {
            Toast.makeText(rootView().getContext(), "时间不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        timer = new Timer();
        timer.schedule(new TerminateTask(mPackageName), times * 60L);
        Toast.makeText(rootView().getContext(), mTitle + "！开始自律！！", Toast.LENGTH_SHORT).show();
    }

    private class TerminateTask extends TimerTask {
        private String packageName;

        public TerminateTask(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public void run() {
            rootView().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(rootView().getContext(), "时间到", Toast.LENGTH_SHORT).show();
                }
            });
            try {
                // 使用 ProcessBuilder 执行 ADB 命令
                // adb shell am force-stop <package-name>
                ProcessBuilder processBuilder = new ProcessBuilder("adb", "shell", "am", "force-stop", packageName);
                Process process = processBuilder.start();

                // 读取命令的输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                // 等待命令执行完毕
                process.waitFor();
            } catch (Exception e) {
                rootView().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(rootView().getContext(), "关闭失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @viewonclick(id = R.id.delete)
    public void deleteApp() {
        PackageManager packageManager = rootView().getContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + mPackageName));
        try {
            packageManager.getPackageInfo(mPackageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(rootView().getContext(), "包名不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (intent.resolveActivity(packageManager) != null) {
            try {
                // 启动卸载活动
                rootView().getContext().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(rootView().getContext(), "无法启动卸载活动", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 没有应用可以处理这个 Intent
            Toast.makeText(rootView().getContext(), "无法找到处理卸载的应用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int layoutId() {
        if (mPackageName.contains("happywork")) {
            return R.layout.app_list_adapter_item_no_delete;
        }
        return R.layout.app_list_adapter_item;
    }
}
