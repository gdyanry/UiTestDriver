package com.yanry.driver.core;

import com.yanry.driver.core.model.runtime.Presentable;
import lib.common.model.json.JSONArray;
import lib.common.model.json.JSONObject;
import lib.common.util.StringUtil;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Utils {
    public static Object getPresentation(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Enum) {
            return ((Enum) obj).name();
        }
        if (obj.getClass().isArray()) {
            JSONArray jsonArray = new JSONArray();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                jsonArray.put(getPresentation(Array.get(obj, i)));
            }
            return jsonArray;
        } else if (obj instanceof List) {
            JSONArray jsonArray = new JSONArray();
            List list = (List) obj;
            for (Object item : list) {
                jsonArray.put(getPresentation(item));
            }
            return jsonArray;
        } else if (obj instanceof Map) {
            JSONArray jsonArray = new JSONArray();
            Map map = (Map) obj;
            for (Object key : map.keySet()) {
                jsonArray.put(new JSONArray().put(getPresentation(key)).put(getPresentation(map.get(key))));
            }
            return jsonArray;
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isAnnotationPresent(Presentable.class)) {
            JSONObject jsonObject = new JSONObject().put(".", StringUtil.getClassName(obj));
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Presentable.class)) {
                    String key = StringUtil.setFirstLetterCase(method.getName().replaceFirst("^get", ""), false);
                    try {
                        Object value = method.invoke(obj);
                        if (value != null) {
                            jsonObject.put(key, getPresentation(value));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            return jsonObject;
        }
        return obj;
    }

    public static boolean isNonNegativeInteger(String str) {
        return str.matches("^([1-9]\\d*)|0$");
    }
}
