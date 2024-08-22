package com.example.forhappywork.dataviewset;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.example.forhappywork.R;
import com.example.forhappywork.baseviewset.BaseAdapterViewSet;
import com.example.guib_annotation.ViewSet;
import com.example.guib_annotation.viewonclick;
import com.example.viewsethelp.bindhelp.ViewSetHelp;
import com.example.viewsethelp.bindhelp.apicenter.ApiCenter;

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

    @viewonclick(id = R.id.item)
    public void itemClick() {
        Toast.makeText(rootView().getContext(), "点击" + mTitle, Toast.LENGTH_SHORT).show();
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
        if (mTitle.contains("HappyWork")) {
            return R.layout.app_list_adapter_item_no_delete;
        }
        return R.layout.app_list_adapter_item;
    }
}
