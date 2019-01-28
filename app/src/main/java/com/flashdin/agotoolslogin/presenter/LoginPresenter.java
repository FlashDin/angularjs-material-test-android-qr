package com.flashdin.agotoolslogin.presenter;

import com.flashdin.agotoolslogin.model.dao.LoginDAO;

import org.json.JSONObject;

public interface LoginPresenter<T extends BasePresenter> extends LoginDAO<JSONObject> {

    void onAttach(T view);

    void onDetach();

    void showFragment();
}
