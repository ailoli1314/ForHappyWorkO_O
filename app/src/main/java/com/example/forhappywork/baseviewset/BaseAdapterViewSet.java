package com.example.forhappywork.baseviewset;


import android.view.View;

import com.example.viewsethelp.bindhelp.iViewSet;

/**
 * Created by v_zhangguibin on 2024/8/21.
 */
public abstract class BaseAdapterViewSet implements iViewSet {

    private iViewSet binder;

    @Override
    public void bindInstance(iViewSet binder) {
        this.binder = binder;
    }

    @Override
    public iViewSet getBinder() {
        return binder;
    }

    @Override
    public void value_change(String name, Object value) {
        binder.value_change(name, value);
    }

    @Override
    public View rootView() {
        return binder.rootView();
    }

    @Override
    public void destory() {
        binder.destory();
    }

    public abstract int layoutId();
}
