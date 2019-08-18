package com.sw.simpleokhttp.interceptor;

import android.util.Log;

import com.sw.simpleokhttp.HttpCodec;
import com.sw.simpleokhttp.HttpConnection;
import com.sw.simpleokhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class CallServiceInterceptor implements Interceptor {
    private static final String TAG = CallServiceInterceptor.class.getSimpleName();

    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Log.i(TAG, "interceptor");
        HttpConnection httpConnection = interceptorChain.getHttpConnection();
        HttpCodec httpCodec = new HttpCodec();
        InputStream inputStream = httpConnection.call(httpCodec);
        // read response line HTTP/1.1 200 OK\r\n
        String statusLine = httpCodec.readLine(inputStream);
        // read response header
        Map<String, String> headers = httpCodec.readHeaders(inputStream);
        // calculate response-body length
        int contentLength = -1;
        if (headers.containsKey(HttpCodec.HEADER_CONTENT_LENGTH)) {
            contentLength = Integer.valueOf(headers.get(HttpCodec.HEADER_CONTENT_LENGTH));
        }
        boolean isChunked = false;
        if (headers.containsKey(HttpCodec.HEADER_TRANSFER_ENCODING)) {
            isChunked = headers.get(HttpCodec.HEADER_TRANSFER_ENCODING).equalsIgnoreCase(HttpCodec.HEADER_CHUNKED);
        }
        String body = null;
        if (contentLength > 0) {
            byte[] bodyBytes = httpCodec.readBytes(inputStream, contentLength);
            body = new String(bodyBytes, HttpCodec.ENCODE_CHARSET);
        } else if (isChunked) {
            body = httpCodec.readChunkedBody(inputStream, contentLength);
        }
        // HTTP/1.1 200 OK\r\n status[0] = "HTTP/1.1",status[1] = "200",status[2] = "OK\r\n"
        String[] status = statusLine.split(" ");
        boolean isKeepAlive = false;
        if (headers.containsKey(HttpCodec.HEADER_CONNECTION)) {
            isKeepAlive = headers.get(httpCodec.HEADER_CONNECTION).equalsIgnoreCase(HttpCodec.HEADER_KEEP_ALIVE);
        }
        httpConnection.updateLastUseTime();
        return new Response(Integer.valueOf(status[1]), contentLength, headers, body, isKeepAlive);
    }
}
