package com.example.forhappywork.baseviewset;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.viewsethelp.bindhelp.ViewSetHelp;
import com.example.viewsethelp.bindhelp.iViewSet;

/**
 * Created by v_zhangguibin on 2024/8/21.
 */
public abstract class BaseActivity extends AppCompatActivity implements iViewSet {

    private iViewSet binder;

    @Override
    public void bindInstance(iViewSet binder) {
        this.binder = binder;
    }

    @Override
    public iViewSet getBinder() {
        return binder;
    }

    /**
     * 基础类型（无地址引用）及其包装类（部分包装类在某个区间有缓存机制，无地址引用），
     * 多个变量同值的情况下，会导致修改到错误的变量方法，这里限定只接受string类型的变量
     * @param variable
     */
    public void value_change(String variable) {
        ViewSetHelp.valueChange(this, variable);
    }

    @Override
    public void value_change(String name, Object value) {
        binder.value_change(name, value);
    }

    @Override
    public View rootView() {
        return getWindow().getDecorView();
    }

    @Override
    public void destory() {
        binder.destory();
    }

}
