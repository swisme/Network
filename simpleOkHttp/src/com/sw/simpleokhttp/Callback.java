package com.sw.simpleokhttp;

public interface Callback {
    void onFailure(Call call, Throwable throwable);

    void onResponse(Call call, Response response);
}
