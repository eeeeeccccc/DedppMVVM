package com.dedpp.dedppmvvm.act;

import android.os.Bundle;

import com.dedpp.dedppmvvm.R;
import com.dedpp.dedppmvvm.base.BaseActivity;
import com.dedpp.dedppmvvm.base.BasePresenter;
import com.dedpp.dedppmvvm.databinding.ActivityMainBinding;
import com.dedpp.dedppmvvm.utils.LogUtil;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainActivity.Presenter> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(); //初始化DataBinding
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void inject() {
        //初始化Presenter对象
        presenter = new Presenter(dataBinding, this);
        //将presenter对象赋予XML中的 data -> variable -> presenter
        dataBinding.setPresenter(presenter);
    }

    public class Presenter extends BasePresenter<ActivityMainBinding, MainActivity> {

        public Presenter(ActivityMainBinding dataBinding, MainActivity activity) {
            super(dataBinding, activity);
        }

        public void goRetrofitRealmDemo() {
            LogUtil.logD("goRetrofitRealmDemo");
            startActivity(RetrofitRealmActivity.createIntent(activity));
        }
    }
}
