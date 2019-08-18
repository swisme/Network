package com.sw.simpleokhttp.interceptor;

import android.util.Log;

import com.sw.simpleokhttp.HttpClient;
import com.sw.simpleokhttp.HttpConnection;
import com.sw.simpleokhttp.HttpUrl;
import com.sw.simpleokhttp.Request;
import com.sw.simpleokhttp.Response;

import java.io.IOException;

public class ConnectionInterceptor implements Interceptor {
    private static final String TAG = ConnectionInterceptor.class.getSimpleName();

    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Request request = interceptorChain.getCall().getRequest();
        HttpClient httpClient = interceptorChain.getCall().getHttpClient();
        HttpUrl httpUrl = request.getHttpUrl();
        HttpConnection httpConnection = httpClient.getConnectionPool().getHttpConnection(httpUrl.getHost(), httpUrl.getPort());
        if (httpConnection == null) {
            httpConnection = new HttpConnection();
        } else {
            Log.i(TAG, "re-use connection");
        }
        httpConnection.setRequest(request);

        try {
            Response response = interceptorChain.proceed(httpConnection);
            if (response.isKeepAlive()) {
                httpClient.getConnectionPool().addConnection(httpConnection);
            } else {
                httpConnection.close();
            }
            return response;
        } catch (IOException e) {
            httpConnection.close();
            throw e;
        }
    }
}
