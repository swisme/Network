package com.sw.simpleokhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

public class HttpConnection {
    private Socket mSocket;
    public long mLastUseTime;
    private Request mRequest;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public void setRequest(Request request) {
        mRequest = request;
    }

    public void updateLastUseTime() {
        mLastUseTime = System.currentTimeMillis();
    }

    public boolean isSameAddress(String host, int port) {
        if (mSocket == null) {
            return false;
        }
        return TextUtils.equals(host, mRequest.getHttpUrl().getHost()) && port == mRequest.getHttpUrl().getPort();
    }

    private void createSocket() throws IOException {
        if (mSocket == null || mSocket.isClosed()) {
            HttpUrl httpUrl = mRequest.getHttpUrl();
            if (httpUrl.getProtocol().equalsIgnoreCase(HttpCodec.PROTOCOL_HTTPS)) {
                mSocket = SSLSocketFactory.getDefault().createSocket();
            } else {
                mSocket = new Socket();
            }
            mSocket.connect(new InetSocketAddress(httpUrl.getHost(), httpUrl.getPort()));
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
        }
    }

    public void close() {
        if (mSocket != null) {
            try {
                if (!mSocket.isClosed()) {
                    mSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (!mSocket.isClosed()) {
                        mSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public InputStream call(HttpCodec httpCodec) throws IOException {
        createSocket();
        httpCodec.writeRequest(mOutputStream, mRequest);
        return mInputStream;
    }
}
