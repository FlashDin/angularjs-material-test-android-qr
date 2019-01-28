package com.flashdin.agotoolslogin.presenter;


import org.json.JSONObject;

public interface BaseView extends BasePresenter {

    void onShowFragment(JSONObject data);

}