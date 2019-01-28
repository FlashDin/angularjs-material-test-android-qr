package com.flashdin.agotoolslogin.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONArrayRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.flashdin.agotoolslogin.R;
import com.flashdin.agotoolslogin.model.config.CustomAdapter;
import com.flashdin.agotoolslogin.model.config.SharedPrefManager;
import com.flashdin.agotoolslogin.model.config.config;
import com.flashdin.agotoolslogin.presenter.BaseView;
import com.flashdin.agotoolslogin.presenter.LoginPresenter;
import com.flashdin.agotoolslogin.presenter.impl.LoginPresenterImpl;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class FormActivity extends AppCompatActivity implements BaseView, SwipeRefreshLayout.OnRefreshListener {

    private LoginPresenter loginPresenter;
    private SharedPrefManager sharedPrefManager;

    @BindView(R.id.btnScan)
    Button btnScan;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    //qr code scanner object
    private IntentIntegrator intentIntegrator;

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    @Override
    public void onRefresh() {
        this.loadData(null);
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_test);
        sharedPrefManager = new SharedPrefManager(this);
//        Bundle bundle = getIntent().getExtras();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
        loginPresenter = new LoginPresenterImpl();
        onAttachView();
        ButterKnife.bind(this);
//        bundle.getString("uname") + " - " + bundle.get("imei");
        loginPresenter.showFragment();
    }

    @Override
    public void onAttachView() {
        loginPresenter.onAttach(this);
    }

    @Override
    public void onDetachView() {
        loginPresenter.onDetach();
    }

    @Override
    public void onShowFragment(JSONObject data) {
        swiperefresh.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swiperefresh.post(new Runnable() {
            @Override
            public void run() {
                swiperefresh.setRefreshing(true);
                loadData(null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        onDetachView();
        super.onDestroy();
    }

    @OnClick(R.id.btnScan)
    public void btnScanClick() {
        // inisialisasi IntentIntegrator(scanQR)
        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("Scan barcode untuk login web");
        intentIntegrator.initiateScan();
    }

    @OnClick(R.id.btnLogout)
    public void btnLogoutClick() {
        AndroidNetworking.post(config.restUrl.getUrl() + "/api/users/logout/{idUser}")
                .addPathParameter("idUser", sharedPrefManager.getSPKey("idUser"))
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
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response okHttpResponse) {
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                        if (okHttpResponse.isSuccessful()) {
                            AndroidNetworking.post(config.restUrl.getUrl() + "/api/usersinbrowser/logout")
                                    .addBodyParameter("idUserD", sharedPrefManager.getSPKey("idUser"))
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
                                    .getAsOkHttpResponseAndJSONArray(new OkHttpResponseAndJSONArrayRequestListener() {
                                        @Override
                                        public void onResponse(Response okHttpResponse, JSONArray response) {
                                            Log.d(TAG, "onResponse object : " + response.toString());
                                            Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            Log.d("Error : ", anError.getMessage());
                                        }
                                    });
                            sharedPrefManager.saveSPString("idUser", "");
                            sharedPrefManager.saveSPString("uname", "");
                            sharedPrefManager.saveSPString("imei", "");
                            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_IS_LOGIN, false);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class)
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
                        Toast.makeText(getApplicationContext(), "Gagal logout", Toast.LENGTH_LONG).show();
                        Log.d("Error : ", anError.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
            } else {
                // jika qrcode berisi data
                try {
                    // converting the data json
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    AndroidNetworking.post(config.restUrl.getUrl() + "/api/usersinbrowser")
                            .addBodyParameter("idUserD", sharedPrefManager.getSPKey("idUser"))
                            .addBodyParameter("fingerprint", jsonObject.get("fingerprint").toString())
                            .addBodyParameter("browser", jsonObject.get("browser").toString())
                            .addBodyParameter("os", jsonObject.get("os").toString())
                            .addBodyParameter("useragent", jsonObject.get("useragent").toString())
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
                                        loadData(null);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    // jika format encoded tidak sesuai maka hasil
                    // ditampilkan ke toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadData(final Bundle savedInstanceState) {
        // showing refresh animation before making http call
        swiperefresh.setRefreshing(true);
        AndroidNetworking.get(config.restUrl.getUrl() + "/api/usersinbrowser/loginlist/{idUser}")
                .addPathParameter("idUser", sharedPrefManager.getSPKey("idUser"))
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
                .getAsOkHttpResponseAndJSONArray(new OkHttpResponseAndJSONArrayRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONArray response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                        mRecyclerView = findViewById(R.id.recycler_view);
                        mLayoutManager = new LinearLayoutManager(FormActivity.this);
                        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                        if (savedInstanceState != null) {
                            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                                    .getSerializable(KEY_LAYOUT_MANAGER);
                        }
                        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
                        mAdapter = new CustomAdapter(response);
                        mRecyclerView.setAdapter(mAdapter);
                        // stopping swipe refresh
                        swiperefresh.setRefreshing(false);
                        if (okHttpResponse.isSuccessful()) {
                            Log.d(TAG, "onResponse success headers : " + okHttpResponse.headers().toString());
                        } else {
                            Log.d(TAG, "onResponse not success headers : " + okHttpResponse.headers().toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        // stopping swipe refresh
                        swiperefresh.setRefreshing(false);
                        Log.d("Error : ", anError.getMessage());
                    }
                });
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

}
