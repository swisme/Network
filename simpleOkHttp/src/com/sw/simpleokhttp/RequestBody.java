package com.sw.simpleokhttp;

import android.util.Log;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    private static final String TAG = RequestBody.class.getSimpleName();
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String CHARSET = "UTF-8";
    private Map<String, String> mBodys = new HashMap<>();

    public RequestBody add(String key, String value) {
        try {
            mBodys.put(URLEncoder.encode(key, CHARSET), URLEncoder.encode(value, CHARSET));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return this;
    }

    public String getBody() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mBodys.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public int getContentLength() {
        return getBody().getBytes().length;
    }
}
