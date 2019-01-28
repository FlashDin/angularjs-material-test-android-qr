package com.flashdin.agotoolslogin.presenter.impl;

import com.flashdin.agotoolslogin.model.dao.LoginDAO;
import com.flashdin.agotoolslogin.model.dao.impl.LoginDAOImpl;
import com.flashdin.agotoolslogin.presenter.BaseView;
import com.flashdin.agotoolslogin.presenter.LoginPresenter;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginPresenterImpl implements LoginPresenter<BaseView> {

    private LoginDAO loginDAO = new LoginDAOImpl();
    private BaseView mBaseView;

    @Override
    public void onAttach(BaseView baseView) {
        mBaseView = baseView;
    }

    @Override
    public void onDetach() {
        mBaseView = null;
    }

    @Override
    public void showFragment() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", "data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mBaseView.onShowFragment(jsonObject);
    }


    @Override
    public JSONObject loginTo(JSONObject params) {
        return (JSONObject) loginDAO.loginTo(params);
    }
}
