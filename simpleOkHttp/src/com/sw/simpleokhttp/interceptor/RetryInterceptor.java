package com.sw.simpleokhttp.interceptor;

import android.util.Log;

import com.sw.simpleokhttp.Call;
import com.sw.simpleokhttp.Response;

import java.io.IOException;

public class RetryInterceptor implements Interceptor {
    private static final String TAG = RetryInterceptor.class.getSimpleName();

    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Log.i(TAG, "intercept");
        Call call = interceptorChain.getCall();
        IOException exception = null;
        for (int i = 0; i < call.getHttpClient().getRetryTimes(); i++) {
            if (call.isCanceled()) {
                throw new IOException("this request has been cancelled");
            }
            try {
                return interceptorChain.proceed();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                exception = e;
            }
        }
        throw exception;
    }
}
