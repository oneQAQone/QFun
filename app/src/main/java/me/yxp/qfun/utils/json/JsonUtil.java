package me.yxp.qfun.utils.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonUtil {

    public static Object findValueByKey(Object jsonObj, String targetKey) {
        if (jsonObj == null || targetKey == null) return null;

        try {
            if (jsonObj instanceof String) {
                String str = ((String) jsonObj).trim();
                if (str.startsWith("[")) {
                    jsonObj = new JSONArray(str);
                } else {
                    jsonObj = new JSONObject(str);
                }
            }

            return searchRecursive(jsonObj, targetKey);
        } catch (Throwable th) {
            return null;
        }
    }

    private static Object searchRecursive(Object obj, String targetKey) throws Throwable {
        if (obj instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) obj;
            if (jsonObj.has(targetKey)) {
                Object value = jsonObj.get(targetKey);
                return value.equals(JSONObject.NULL) ? null : value;
            }

            for (Iterator<String> it = jsonObj.keys(); it.hasNext(); ) {
                String key = it.next();
                Object result = searchRecursive(jsonObj.get(key), targetKey);
                if (result != null) return result;
            }
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            for (int i = 0; i < array.length(); i++) {
                Object result = searchRecursive(array.get(i), targetKey);
                if (result != null) return result;
            }
        }

        return null;
    }
}