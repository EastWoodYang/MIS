package com.eastwood.common.mis;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import java.lang.ref.WeakReference;

public class MisService {

    private static ArrayMap<Class, Object> sServiceArrayMap;
    private static ArrayMap<Class, WeakReference<Object>> sWeekServiceArrayMap;

    public static void register(@NonNull Class serviceKey, @NonNull Object serviceObjectOrClass) {
        if (!serviceKey.isInterface()) {
            throw new IllegalArgumentException("register service key must be interface class.");
        }

        if (serviceObjectOrClass.getClass().isInterface()) {
            throw new IllegalArgumentException("register service object must not be interface.");
        }

        Class realClass = serviceObjectOrClass instanceof Class ? (Class) serviceObjectOrClass : serviceObjectOrClass.getClass();
        if (!serviceKey.isAssignableFrom(realClass)) {
            throw new IllegalArgumentException(String.format("register service object must implement interface %s.", serviceKey));
        }

        if (sServiceArrayMap == null) {
            sServiceArrayMap = new ArrayMap<>();
        }
        sServiceArrayMap.put(serviceKey, serviceObjectOrClass);
    }

    public static void unregister(Class serviceKey) {
        if (serviceKey == null || sServiceArrayMap == null) return;
        sServiceArrayMap.remove(serviceKey);
    }

    public static <T> T getService(Class<T> serviceKey) {
        if (sServiceArrayMap == null) return null;

        Object object = sServiceArrayMap.get(serviceKey);
        if (object == null) return null;

        if (object instanceof Class) {
            Object result = null;
            if (sWeekServiceArrayMap == null) {
                sWeekServiceArrayMap = new ArrayMap<>();
            }
            WeakReference<Object> cachedObject = sWeekServiceArrayMap.get(serviceKey);
            if (cachedObject != null && cachedObject.get() != null) {
                result = cachedObject.get();
            } else {
                try {
                    result = ((Class) object).newInstance();
                    sWeekServiceArrayMap.put(serviceKey, new WeakReference<>(result));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
            return (T) result;
        } else {
            return (T) object;
        }
    }

}