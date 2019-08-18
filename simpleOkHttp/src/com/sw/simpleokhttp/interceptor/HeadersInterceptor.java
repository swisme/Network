package com.sw.simpleokhttp.interceptor;

import android.text.TextUtils;
import android.util.Log;

import com.sw.simpleokhttp.HttpCodec;
import com.sw.simpleokhttp.Request;
import com.sw.simpleokhttp.RequestBody;
import com.sw.simpleokhttp.Response;

import java.io.IOException;
import java.util.Map;

public class HeadersInterceptor implements Interceptor {
    private static final String TAG = HeadersInterceptor.class.getSimpleName();

    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Log.i(TAG, "intercept");
        Request request = interceptorChain.getCall().getRequest();
        Map<String, String> headers = request.getHeaders();
        if (!headers.containsKey(HttpCodec.HEADER_HOST)) {
            headers.put(HttpCodec.HEADER_HOST, request.getHttpUrl().getHost());
        }

        if (!headers.containsKey(HttpCodec.HEADER_CONNECTION)) {
            headers.put(HttpCodec.HEADER_CONNECTION, HttpCodec.HEADER_KEEP_ALIVE);
        }

        RequestBody body = request.getBody();
        if (body != null) {
            String contentType = body.getContentType();
            if (!TextUtils.isEmpty(contentType)) {
                headers.put(HttpCodec.HEADER_CONTENT_TYPE, contentType);
            }
            int contentLength = body.getContentLength();
            if (contentLength != -1) {
                headers.put(HttpCodec.HEADER_CONTENT_LENGTH, Long.toString(contentLength));
            }
        }
        return interceptorChain.proceed();
    }
}
