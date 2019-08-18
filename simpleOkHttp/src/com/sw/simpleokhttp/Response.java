package com.sw.simpleokhttp;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int mCode;
    private int mContentLength = -1;
    private Map<String, String> mHeaders = new HashMap<>();
    private String mBody;
    private boolean mIsKeepAlive;

    public Response() {
    }

    public Response(int code, int contentLength, Map<String, String> headers, String body, boolean isKeepAlive) {
        mCode = code;
        mContentLength = contentLength;
        mHeaders = headers;
        mBody = body;
        mIsKeepAlive = isKeepAlive;
    }

    public int getCode() {
        return mCode;
    }

    public int getContentLength() {
        return mContentLength;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public String getBody() {
        return mBody;
    }

    public boolean isKeepAlive() {
        return mIsKeepAlive;
    }
}
