package com.chevstrap.rbx;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonFormatter {
    public static String formatJson(String json) {
        try {
            if (json.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.toString(4);
            } else if (json.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                return jsonArray.toString(4);
            } else {
                return "Invalid JSON";
            }
        } catch (JSONException e) {
            return "Invalid JSON";
        }
    }
}
