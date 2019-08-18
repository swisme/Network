package com.sw.simpleokhttp;

import android.text.TextUtils;

import java.net.URL;

public class HttpUrl {
    private String mHost;
    private String mProtocol;
    private int mPort;
    private String mFile;
//    private String mPath;
//    private String mQuery;
//    private String mRef;
//    private String mAuthority;
//    private String mUserInfo;
//    private Object mContent;

    public HttpUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        mHost = url.getHost();
        mProtocol = url.getProtocol();
        mPort = url.getPort();
        mFile = url.getFile();
//        mContent = url.getContent();
//        mPath = url.getPath();
//        mQuery = url.getQuery();
//        mRef = url.getRef();
//        mAuthority = url.getAuthority();
//        mUserInfo = url.getUserInfo();
        if (mPort == -1) {
            mPort = url.getDefaultPort();
        }
        if (TextUtils.isEmpty(mFile)) {
            mFile = "/";
        }
    }

    public String getHost() {
        return mHost;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public int getPort() {
        return mPort;
    }

    public String getFile() {
        return mFile;
    }

//    public String getPath() {
//        return mPath;
//    }
//
//    public String getQuery() {
//        return mQuery;
//    }
//
//    public String getRef() {
//        return mRef;
//    }
//
//    public String getAuthority() {
//        return mAuthority;
//    }
//
//    public String getUserInfo() {
//        return mUserInfo;
//    }
//
//    public Object getContent() {
//        return mContent;
//    }
}
