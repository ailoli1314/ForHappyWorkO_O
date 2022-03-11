package com.example.viewsethelp.bindhelp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.example.viewsethelp.bindhelp.apicenter.Api;
import com.example.viewsethelp.bindhelp.apicenter.ApiCenter;
import com.example.viewsethelp.bindhelp.apicenter.ApiExecutor;

import android.app.Activity;
import android.view.View;
import androidx.fragment.app.Fragment;

public class ViewSetHelp {
    public static String classFoot = "_Binding";

    public static ApiCenter apiCenter;

    public static void bind(Fragment target) {
        bind(target, target.getView());
    }

    public static void bind(Activity target) {
        bind(target, target.getWindow().getDecorView());
    }

    public static void bind(Object target, View view) {
        Class<?> targetClass = target.getClass();
        String clsName = targetClass.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return;
        }
        try {
            Class<?> bindingClass = targetClass.getClassLoader().loadClass(clsName + "_Binding");
            Constructor<?> bindingCtor = bindingClass.getConstructor(targetClass, View.class);
            bindingCtor.newInstance(target, view);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (apiCenter == null) {
            apiCenter = new ApiCenter();
        }
    }

    public static Api getApi() {
        if (apiCenter == null) {
            apiCenter = new ApiCenter();
        }
        return Api.obtain(apiCenter);
    }

    public static void register(ApiExecutor apiExecutor) {
        if (apiCenter == null) {
            apiCenter = new ApiCenter();
        }
        apiCenter.register(apiExecutor.Target(), apiExecutor);
    }

    public static void register(String key, ApiExecutor apiExecutor) {
        if (apiCenter == null) {
            apiCenter = new ApiCenter();
        }
        apiCenter.register(key, apiExecutor);
    }

    public static void unRegister(String key) {
        if (apiCenter == null) {
            return;
        }
        apiCenter.unregister(key);
    }
}
