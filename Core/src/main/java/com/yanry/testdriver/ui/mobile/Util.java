package com.yanry.testdriver.ui.mobile;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import lib.common.model.json.JSONArray;
import lib.common.model.json.JSONObject;
import lib.common.util.StringUtil;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by rongyu.yan on 3/7/2017.
 */
public class Util {
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
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isAnnotationPresent(Presentable.class)) {
            JSONObject jsonObject = new JSONObject().put(".", StringUtil.getClassName(obj));
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Presentable.class)) {
                    String key = StringUtil.setFirstLetterCase(method.getName().replaceFirst("^get", ""), false);
                    try {
                        method.setAccessible(true);
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
            if (obj instanceof Map) {
                JSONArray jsonArray = new JSONArray();
                Map map = (Map) obj;
                for (Object key : map.keySet()) {
                    jsonArray.put(new JSONArray().put(getPresentation(key)).put(getPresentation(map.get(key))));
                }
                jsonObject.put("{}", jsonArray);
            }
            return jsonObject;
        }
        return obj;
    }

    public static Path createPath(Graph graph, Event event, Expectation expectation) {
        Path path = new Path(event, expectation);
        path.put(graph.getProcessState(), true);
        graph.addPath(path);
        return path;
    }
}
