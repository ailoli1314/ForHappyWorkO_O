package com.example.viewsethelp.bindhelp.apicenter;

import java.util.concurrent.ConcurrentHashMap;

public class ApiCenter {
    private final ConcurrentHashMap<Object, ApiExecutor> executors = new ConcurrentHashMap<>();

    public void call(Api api) {
        getApiExecutor(api).execute(api);
        api.recycle();
    }

    public void post(final Api api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getApiExecutor(api).execute(api);
                api.recycle();
            }
        }).start();
    }

    public ApiExecutor getApiExecutor(Api api) {
        return executors.get(api.target());
    }

    public void register(Object target, ApiExecutor apiExecutor) {
        if (target == null || apiExecutor == null) {
            return;
        }

        executors.put(target, apiExecutor);
    }

    public void unregister(Object target) {
        if (target == null) {
            return;
        }
        executors.remove(target);
    }
}
