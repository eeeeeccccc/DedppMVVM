package com.dedpp.dedppmvvm.base;

import android.databinding.ViewDataBinding;

import io.realm.Realm;

/**
 * BasePresenter
 * Created by Dedpp on 2017/9/6.
 */

public abstract class BasePresenter<T extends ViewDataBinding, A extends BaseActivity> {

    protected T dataBinding;
    protected A activity;
    protected Realm realm;

    public BasePresenter(T dataBinding, A activity) {
        this.dataBinding = dataBinding;
        this.activity = activity;
    }

    /**
     * 初始化页面
     */
    protected void initView() {
    }

    protected void destroy() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
        dataBinding = null;
        activity = null;
    }

}
