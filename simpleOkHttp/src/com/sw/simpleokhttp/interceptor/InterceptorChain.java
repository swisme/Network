package com.sw.simpleokhttp.interceptor;

import com.sw.simpleokhttp.Call;
import com.sw.simpleokhttp.HttpConnection;
import com.sw.simpleokhttp.Response;

import java.io.IOException;
import java.util.List;

public class InterceptorChain {
    private final List<Interceptor> mInterceptors;
    private final int mIndex;
    private final Call mCall;
    private HttpConnection mHttpConnection;

    public InterceptorChain(List<Interceptor> interceptors, int index, Call call, HttpConnection connection) {
        mInterceptors = interceptors;
        mIndex = index;
        mCall = call;
        mHttpConnection = connection;
    }

    public Response proceed(HttpConnection httpConnection) throws IOException {
        mHttpConnection = httpConnection;
        return proceed();
    }

    public Call getCall() {
        return mCall;
    }

    public HttpConnection getHttpConnection() {
        return mHttpConnection;
    }

    public Response proceed() throws IOException {
        if (mIndex >= mInterceptors.size()) {
            throw new IOException("Interceptor Chain out of bounds");
        }
        Interceptor interceptor = mInterceptors.get(mIndex);
        InterceptorChain nextChain = new InterceptorChain(mInterceptors, mIndex + 1, mCall, mHttpConnection);
        return interceptor.intercept(nextChain);
    }
}
