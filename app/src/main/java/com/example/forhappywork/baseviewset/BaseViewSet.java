package com.example.forhappywork.baseviewset;


import com.example.viewsethelp.bindhelp.iViewSet;

/**
 * Created by v_zhangguibin on 2024/8/21.
 */
public abstract class BaseViewSet implements iViewSet {

    private iViewSet binder;

    @Override
    public void bindInstance(iViewSet binder) {
        this.binder = binder;
    }

    @Override
    public iViewSet getBinder() {
        return binder;
    }
}
