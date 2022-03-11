package com.example.viewsethelp.bindhelp.apicenter;

import android.util.ArrayMap;
import androidx.annotation.NonNull;

public class Api {
    private int what;
    private String target;
    private ArrayMap<String, Object> values;
    public  ApiCenter apiCenter;

    Api next;
    private static final Object POOL_LOCK = new Object();
    // 消息对象池大小
    private static final int MAX_POOL_SIZE = 50;
    // 当前的参数池内参数数量
    private static int poolSize = 0;
    // 使用参数池，实现反复利用
    private static Api sPool;

    @NonNull
    private static Api obtain() {
        synchronized (POOL_LOCK) {
            if (sPool != null) {
                Api m = sPool;
                sPool = sPool.next;
                m.next = null;
                poolSize--;
                return m;
            }
        }
        return new Api();
    }

    @NonNull
    public static Api obtain(ApiCenter apiCenter) {
        Api api = obtain();
        api.apiCenter = apiCenter;
        return api;
    }

    public String target() {
        return target;
    }

    public Api find(int tag) {
        this.what = tag;
        return this;
    }

    public Api set(String key, Object value) {
        if (values == null) {
            values = new ArrayMap<>();
        }
        values.put(key, value);
        return this;
    }

    public Object get(String key) {
        if (values == null) {
            return null;
        }
        return values.get(key);
    }

    /**
     * 设置Api请求的目标ApiExecutor
     *
     * @param target 目标ApiExecutor对应的key
     * @return api
     */
    public Api to(String target) {
        this.target = target;
        return this;
    }

    public void call() {
        if (apiCenter == null) {
            return;
        }
        apiCenter.call(this);
    }

    public void post() {
        if (apiCenter == null) {
            return;
        }
        apiCenter.post(this);
    }

    public int what() {
        return what;
    }

    /**
     * 回收一个Message对象
     */
    void recycle() {
        if (values != null) {
            values.clear();
        }
        what = 0;
        next = null;
        apiCenter = null;
        target = null;

        synchronized (POOL_LOCK) {
            if (poolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                poolSize++;
            }
        }
    }

}
