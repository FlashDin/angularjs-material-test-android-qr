package com.flashdin.agotoolslogin.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.flashdin.agotoolslogin.R;
import com.flashdin.agotoolslogin.model.config.SharedPrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends Activity {

    private SharedPrefManager sharedPrefManager;
    @BindView(R.id.lbUname)
    TextView lbUname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPrefManager = new SharedPrefManager(this);
        ButterKnife.bind(this);
        lbUname.setText("Hi " + sharedPrefManager.getSPKey("uname"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), FormActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("uname", sharedPrefManager.getSPKey("uname"));
//                intent.putExtra("imei", sharedPrefManager.getSPKey("imei"));
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
