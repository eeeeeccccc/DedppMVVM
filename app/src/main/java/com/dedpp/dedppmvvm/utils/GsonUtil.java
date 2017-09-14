package com.dedpp.dedppmvvm.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * GsonUtil
 * Created by linzhixin on 2017/7/28.
 */

public class GsonUtil {

    private static Gson gson;

    private GsonUtil() {
    }

    private static synchronized void init() {
        if (gson == null) {
            gson = new Gson();
        }
    }

    public static <T> String toString(T model) {
        if (gson == null)
            init();
        return gson.toJson(model);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (gson == null)
            init();
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException je) {
            je.printStackTrace();
            return null;
        }
    }

}
