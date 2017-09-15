package com.dedpp.dedppmvvm.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * BaseActivity
 * Created by Dedpp on 2017/9/4.
 */

public abstract class BaseActivity<T extends ViewDataBinding, P extends BasePresenter> extends RxAppCompatActivity {

    protected T dataBinding;
    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        inject();
        presenter.initView();
    }

    protected void initDataBinding() {
        int layoutId = getLayoutRes();
        if (layoutId == 0) {
            try {
                throw new Exception("Activity layoutId can't 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dataBinding = DataBindingUtil.setContentView(this, layoutId);
    }

    /**
     * 传入布局文件
     *
     * @return 基类会自动生成对应的DataBinding供导出类使用
     */
    protected abstract int getLayoutRes();

    /**
     * 初始化DataBinding
     */
    protected abstract void inject();

    public void rxClick(View view, Consumer<Object> consumer) {
        RxView.clicks(view)
                .throttleFirst(2, TimeUnit.SECONDS)// 两秒钟之内只取一个点击事件，防抖操作
                .compose(bindToLifecycle())
                .subscribe(consumer);
    }

    @Override
    protected void onDestroy() {
        if (dataBinding != null) {
            dataBinding.unbind();
            presenter.destroy();
            dataBinding = null;
            presenter = null;
        }
        super.onDestroy();
    }

}
