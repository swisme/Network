package com.sw.simpleokhttp;

import com.sw.simpleokhttp.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class HttpClient {
    private Dispatcher mDispatcher;
    private List<Interceptor> mInterceptors;
    private int mRetryTimes;
    private ConnectionPool mConnectionPool;

    public HttpClient(Builder builder) {
        mDispatcher = builder.dispatcher;
        mInterceptors = builder.interceptors;
        mRetryTimes = builder.retryTimes;
        mConnectionPool = builder.connectionPool;
    }

    public Call newCall(Request request) {
        return new Call(this, request);
    }

    public Dispatcher getDispatcher() {
        return mDispatcher;
    }

    public List<Interceptor> getInterceptors() {
        return mInterceptors;
    }

    public ConnectionPool getConnectionPool() {
        return mConnectionPool;
    }

    public int getRetryTimes() {
        return mRetryTimes;
    }

    public static final class Builder {
        Dispatcher dispatcher;
        List<Interceptor> interceptors = new ArrayList<>();
        int retryTimes;
        ConnectionPool connectionPool;

        public HttpClient build() {
            if (dispatcher == null) {
                dispatcher = new Dispatcher();
            }
            if (connectionPool == null) {
                connectionPool = new ConnectionPool();
            }
            return new HttpClient(this);
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public Builder setDispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public Builder setRetryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        public Builder setConnectionPool(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
            return this;
        }
    }
}
