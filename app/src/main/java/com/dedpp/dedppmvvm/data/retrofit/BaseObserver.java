package com.dedpp.dedppmvvm.data.retrofit;

import com.blankj.utilcode.util.ToastUtils;
import com.dedpp.dedppmvvm.utils.LogUtil;
import com.dedpp.dedppmvvm.BuildConfig;
import com.dedpp.dedppmvvm.data.model.BaseEntity;
import com.dedpp.dedppmvvm.utils.GsonUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * BaseObserver
 * Created by Dedpp on 2017/9/4.
 */
public abstract class BaseObserver<T> implements Observer<BaseEntity<T>> {

    @Override
    public void onSubscribe(Disposable d) {
        LogUtil.logD("onSubscribe()");
        onHandleStart();
    }

    @Override
    public void onNext(BaseEntity<T> value) {
        LogUtil.logD("onNext(): " + GsonUtil.toString(value));
        if (value.getErrorCode() == 0) {
            T t = value.getData();
            onHandleSuccess(t);
        } else {
            onHandleFailure(value);
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.logE("onError(): " + e.toString());
        onHandleError(e);
    }

    @Override
    public void onComplete() {
        LogUtil.logD("onComplete()");
        onHandleComplete();
    }

    protected void onHandleStart() {
    }

    protected abstract void onHandleSuccess(T t);

    protected void onHandleFailure(BaseEntity entity) {
        if (BuildConfig.DEBUG) {
            ToastUtils.showShort(entity.getErrorMsg() + "\n" + entity.getErrorCode() + " | " + entity.getRequestId());
        } else {
            ToastUtils.showShort(entity.getErrorMsg());
        }
    }

    protected void onHandleError(Throwable e) {
    }

    protected void onHandleComplete() {
    }

}
