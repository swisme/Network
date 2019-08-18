package com.sw.simpleokhttp;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Map<String, String> mHeaders;
    private RequestMethod mRequestMethod;
    private HttpUrl mHttpUrl;
    private RequestBody mRequestBody;

    public Request(Builder builder) {
        mHeaders = builder.headers;
        mRequestBody = builder.requestBody;
        mHttpUrl = builder.httpUrl;
        mRequestMethod = builder.requestMethod;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public HttpUrl getHttpUrl() {
        return mHttpUrl;
    }

    public RequestMethod getMethod() {
        return mRequestMethod;
    }

    public RequestBody getBody() {
        return mRequestBody;
    }

    public static final class Builder {
        Map<String, String> headers = new HashMap<>();
        RequestMethod requestMethod;
        HttpUrl httpUrl;
        RequestBody requestBody;

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder removeHeader(String key) {
            headers.remove(key);
            return this;
        }

        public Builder post(RequestBody requestBody) {
            this.requestBody = requestBody;
            this.requestMethod = RequestMethod.POST;
            return this;
        }

        public Builder get() {
            this.requestMethod = RequestMethod.GET;
            return this;
        }

        public Builder url(String url) {
            try {
                this.httpUrl = new HttpUrl(url);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("invalid url", e);
            }
        }

        public Request build() {
            if (httpUrl == null) {
                throw new IllegalStateException("url is null");
            }
            if (requestMethod == null) {
                requestMethod = RequestMethod.POST;
            }
            return new Request(this);
        }
    }
}
