package com.example.viewsethelp.bindhelp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.example.guib_annotation.ViewSet;
import com.example.viewsethelp.bindhelp.apicenter.Api;
import com.example.viewsethelp.bindhelp.apicenter.ApiCenter;
import com.example.viewsethelp.bindhelp.apicenter.ApiExecutor;

import android.util.Log;
import android.view.View;

public class ViewSetHelp {

    static final String TAG = "ViewSetHelp";

    public static String classFoot = "_Binder";

    public static ApiCenter apiCenter;

    public static <T extends iViewSet> void bind(T target) {
        bind(target, target.rootView());
    }

    public static <T extends iViewSet> void bind(T target, View view) {
        Class<?> targetClass = target.getClass();
        String clsName = targetClass.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return;
        }
        try {
            Class<?> bindingClass = targetClass.getClassLoader().loadClass(clsName + classFoot);
            Constructor<?> bindingCtor = bindingClass.getConstructor(iViewSet.class, View.class);
            iViewSet binder = (iViewSet) bindingCtor.newInstance(target, view);
            target.bindInstance(binder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apiCenter == null) {
            apiCenter = new ApiCenter();
        }
    }

    public static <T extends iViewSet, V> void valueChange(T target, V variable) {
        valueChange(target, variable, variable);
    }

    public static <T extends iViewSet, V> void valueChange(T target, V variable, V value) {
        target.getBinder().value_change(getVariableName(target, variable), value.toString());
        setVariableValue(target, variable, variable);
    }

    /**
     *
     * @param target
     * @param variable 基础类型（无地址引用）及其包装类（部分包装类在某个区间有缓存机制，无地址引用），导致getVariableName可能获取不到正确的变量名
     * @return
     * @param <T>
     */
    public static <T extends iViewSet> String getVariableName(T target, Object variable) {
        if (variable.getClass().isPrimitive()) {
            Log.e(TAG, "Primitive variable cant use valueChange");
            return null;
        }
        Field[] fields = target.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[5].getName();
            try {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(target);
                if (value == variable) {
                    ViewSet annotation = field.getAnnotation(ViewSet.class);
                    if (annotation == null) {
                        continue;
                    }
                    if (annotation.type() == ViewSet.ViewType.TV) {
                        return field.getName() + "_tv";
                    }
                    if (annotation.type() == ViewSet.ViewType.IV) {
                        return field.getName() + "_iv";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static  <T extends iViewSet> void setVariableValue(T target, Object variable, Object value) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object variableValue = field.get(target);
                if (variableValue == variable) {
                    field.set(target, value);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
