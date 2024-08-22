package com.example.viewsethelp.bindhelp;

import android.view.View;

public interface iViewSet {
    void bindInstance(iViewSet binder);

    iViewSet getBinder();

    void value_change(String name, Object value);

    View rootView();
}
