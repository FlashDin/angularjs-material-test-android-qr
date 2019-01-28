package com.flashdin.agotoolslogin.model.dao.impl;

import android.os.Looper;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.flashdin.agotoolslogin.model.config.config;
import com.flashdin.agotoolslogin.model.dao.LoginDAO;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class LoginDAOImpl implements LoginDAO<JSONObject> {

    public static final String restUrl = config.restUrl.getUrl();

    @Override
    public JSONObject loginTo(JSONObject params) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uname", params.get("txtUname"));
            jsonObject.put("pass", params.get("txtPass"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.put(restUrl + "/login")
                .addJSONObjectBody(jsonObject)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {
                        Log.d(TAG, "onResponse object : " + response);
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                        if (okHttpResponse.isSuccessful()) {
                            Log.d(TAG, "onResponse success headers : " + okHttpResponse.headers().toString());
                        } else {
                            Log.d(TAG, "onResponse not success headers : " + okHttpResponse.headers().toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Error : ", anError.getMessage());
                    }
                });

        return jsonObject;
    }
}
