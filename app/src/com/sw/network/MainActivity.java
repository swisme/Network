package com.sw.network;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.sw.simpleokhttp.Call;
import com.sw.simpleokhttp.Callback;
import com.sw.simpleokhttp.HttpClient;
import com.sw.simpleokhttp.Request;
import com.sw.simpleokhttp.RequestBody;
import com.sw.simpleokhttp.Response;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == 0){
                testNet();
            }
        }

    }

    private void testNet() {
        HttpClient client = new HttpClient.Builder().setRetryTimes(3).build();
        String postUrl = "http://restapi.amap.com/v3/weather/weatherInfo";
        String getUrl = "http://www.kuaidi100.com/query?type=yuantong&postid=222222222";
        String getUrlBaidu = "https://www.baidu.com";
        String getUrlCSDN = "https://blog.csdn.net/m13666368773/article/details/7245675";
        RequestBody body = new RequestBody().add("key", "064a7778b8389441e30f91b8a60c9b23")
                .add("city", "深圳");
        Request request = new Request.Builder().post(body).url(getUrlCSDN).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, Throwable throwable) {
                Log.e(TAG, "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.e(TAG, "response : " + response.getBody());
            }
        });
    }
}
