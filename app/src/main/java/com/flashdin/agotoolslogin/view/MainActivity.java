package com.flashdin.agotoolslogin.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.flashdin.agotoolslogin.R;
import com.flashdin.agotoolslogin.model.config.SharedPrefManager;
import com.flashdin.agotoolslogin.model.config.config;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SharedPrefManager sharedPrefManager;
    @BindView(R.id.txtUname)
    TextView txtUname;
    @BindView(R.id.txtPass)
    TextView txtPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefManager = new SharedPrefManager(this);
        if (sharedPrefManager.getSPIsLogin()) {
            Intent intent = new Intent(this.getApplicationContext(), FormActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("uname", sharedPrefManager.getSPKey("uname"));
//            intent.putExtra("imei", sharedPrefManager.getSPKey("imei"));
            startActivity(intent);
            finish();
        }
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogin)
    public void btnLoginClick() {
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(this.txtUname.getText())) {
            this.txtUname.setError(getString(R.string.error_field_required));
            focusView = this.txtUname;
            cancel = true;
        } else if (TextUtils.isEmpty(this.txtPass.getText())) {
            this.txtPass.setError(getString(R.string.error_field_required));
            focusView = this.txtPass;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            AndroidNetworking.post(config.restUrl.getUrl() + "/api/users/login")
                    .addBodyParameter("uname", this.txtUname.getText().toString())
                    .addBodyParameter("pass", this.txtPass.getText().toString())
                    .addBodyParameter("imei", this.getIMEI())
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
                    .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                        @Override
                        public void onResponse(Response okHttpResponse, JSONObject response) {
                            Log.d(TAG, "onResponse object : " + response.toString());
                            Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                            if (okHttpResponse.isSuccessful()) {
                                try {
                                    sharedPrefManager.saveSPString("idUser", response.get("idUser").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sharedPrefManager.saveSPString("uname", txtUname.getText().toString());
                                sharedPrefManager.saveSPString("imei", getIMEI());
                                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_IS_LOGIN, true);
                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Log.d(TAG, "onResponse success headers : " + okHttpResponse.headers().toString());
                            } else {
                                Log.d(TAG, "onResponse not success headers : " + okHttpResponse.headers().toString());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            txtUname.setError("Username / Password tidak sesuai");
                            View focusView = txtUname;
                            focusView.requestFocus();
                            Log.d("Error : ", anError.getMessage());
                        }
                    });
        }
    }

    private String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        return telephonyManager.getDeviceId();
    }

    private String getDeviceUniqueID() {
        String device_unique_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        return device_unique_id;
    }

}