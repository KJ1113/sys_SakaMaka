package com.example.leehangeol.sakamaka;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class JsonPaser {
    public JSONArray jarray;
    public JSONObject jobject;
    public JsonPaser() {
    }
    public abstract void inputArray(String str);
    public JSONObject getJobject(){return jobject;};
}