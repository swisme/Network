package com.sw.simpleokhttp.interceptor;

import com.sw.simpleokhttp.Response;

import java.io.IOException;

public interface Interceptor {
    Response intercept(InterceptorChain interceptorChain) throws IOException;
}
