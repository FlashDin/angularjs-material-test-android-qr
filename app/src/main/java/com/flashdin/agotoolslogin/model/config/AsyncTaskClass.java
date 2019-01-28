package com.flashdin.agotoolslogin.model.config;

import android.content.Context;
import android.os.AsyncTask;

public class AsyncTaskClass  extends AsyncTask<Void, Void, Void> {

    private Context context;

    public AsyncTaskClass(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // before making http calls

    }

    @Override
    protected Void doInBackground(Void... arg0) {
//        execute command
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // After completing command
    }

}