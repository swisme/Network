package com.sw.simpleokhttp;

import com.sw.simpleokhttp.interceptor.CallServiceInterceptor;
import com.sw.simpleokhttp.interceptor.ConnectionInterceptor;
import com.sw.simpleokhttp.interceptor.HeadersInterceptor;
import com.sw.simpleokhttp.interceptor.Interceptor;
import com.sw.simpleokhttp.interceptor.InterceptorChain;
import com.sw.simpleokhttp.interceptor.RetryInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Call {
    private HttpClient mHttpClient;
    private Request mRequest;
    private boolean mExecuted;
    private boolean mCanceled;

    public Call(HttpClient httpClient, Request request) {
        mHttpClient = httpClient;
        mRequest = request;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public HttpClient getHttpClient() {
        return mHttpClient;
    }

    public Request getRequest() {
        return mRequest;
    }

    public boolean isExecuted() {
        return mExecuted;
    }

    Response getResponse() throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(mHttpClient.getInterceptors());
        interceptors.add(new RetryInterceptor());
        interceptors.add(new HeadersInterceptor());
        interceptors.add(new ConnectionInterceptor());
        interceptors.add(new CallServiceInterceptor());
        InterceptorChain interceptorChain = new InterceptorChain(interceptors, 0, this, null);
        return interceptorChain.proceed();
    }

    public Call enqueue(Callback callback) {
        synchronized (this) {
            if (mExecuted) {
                throw new IllegalStateException("This call has already been executed");
            }
            mExecuted = true;
        }
        mHttpClient.getDispatcher().enqueue(new AsyncCall(callback));
        return this;
    }

    final class AsyncCall implements Runnable {
        private Callback callback;

        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                Response response = getResponse();
                if (mCanceled) {
                    callback.onFailure(Call.this, new IOException("this request has been cancelled"));
                } else {
                    callback.onResponse(Call.this, response);
                }
            } catch (IOException e) {
                callback.onFailure(Call.this, e);
            } finally {
                mHttpClient.getDispatcher().finished(this);
            }
        }

        public String getHost() {
            return mRequest.getHttpUrl().getHost();
        }
    }
}
