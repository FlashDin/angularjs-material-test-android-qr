package com.flashdin.agotoolslogin.model.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.flashdin.agotoolslogin.R;

public class SharedPrefManager {

    public static final String SP_IS_LOGIN = "spIsLogin";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPKey(String key){
        return sp.getString(key, "");
    }

    public Boolean getSPIsLogin(){
        return sp.getBoolean(SP_IS_LOGIN, false);
    }

}
