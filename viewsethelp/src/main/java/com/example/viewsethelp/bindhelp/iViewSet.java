package com.example.viewsethelp.bindhelp;

import android.view.View;

public interface iViewSet {

    /**
     * 返回设置生成的绑定类
     * @param binder 编译器生成的绑定类
     */
    void bindInstance(iViewSet binder);

    /**
     * 获取绑定类
     * @return
     */
    iViewSet getBinder();

    /**
     *
     * @param name 绑定的变量名称
     * @param value 变量的修改值
     */
    void value_change(String name, Object value);

    /**
     * 根view
     * @return
     */
    View rootView();

    void destory();
}
