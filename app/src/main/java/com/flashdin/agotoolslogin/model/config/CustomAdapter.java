/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flashdin.agotoolslogin.model.config;

import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.flashdin.agotoolslogin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import okhttp3.Response;

/**
 * Provide views to RecyclerView with data from mList.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    JSONArray mList;
    JSONObject mItem;

    public CustomAdapter(JSONArray ls) {
        mList = ls;
    }

    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imvBrowser)
        ImageView imvBrowser;
        @BindView(R.id.lbBrowser)
        TextView lbBrowser;
        @BindView(R.id.lbOs)
        TextView lbOs;
        @BindView(R.id.lbUseragent)
        TextView lbUseragent;
        private SharedPrefManager sharedPrefManager;

        public ViewHolder(View v) {
            super(v);
            sharedPrefManager = new SharedPrefManager(v.getContext());
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.btnLogoutWeb)
        public void btnLogoutWebClick(View v) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("idUserD", sharedPrefManager.getSPKey("idUser"));
                jsonObject.put("fingerprint", mList.getJSONObject(getAdapterPosition()).get("fingerprint"));
                this.logoutFromWeb(jsonObject, getAdapterPosition());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void logoutFromWeb(JSONObject jsonObject, final int position) {
            try {
                AndroidNetworking.post(config.restUrl.getUrl() + "/api/usersinbrowser/logout")
                        .addBodyParameter("idUserD", jsonObject.get("idUserD").toString())
                        .addBodyParameter("fingerprint", jsonObject.get("fingerprint").toString())
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
                                    mList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, mList.length());
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
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listcontent, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        try {
            mItem = mList.getJSONObject(position);
//            byte[] decodeString = Base64.decode(mItem.get("browserImg").toString(), Base64.DEFAULT);
//            Bitmap decodeByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
//            decodeByte = Bitmap.createScaledBitmap(decodeByte, 75, 75, true);
//            viewHolder.getImvBrowser().setImageBitmap(decodeByte);
            viewHolder.getLbBrowser().setText(String.valueOf(mItem.get("browser")));
            viewHolder.getLbOs().setText(String.valueOf(mItem.get("os")));
            viewHolder.getLbUseragent().setText(String.valueOf(mItem.get("useragent")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.length();
    }
}
