package com.dedpp.dedppmvvm.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.dedpp.dedppmvvm.utils.LogUtil;
import com.dedpp.dedppmvvm.utils.SPKeys;
import com.dedpp.dedppmvvm.data.model.CarBrandEntity;
import com.dedpp.dedppmvvm.data.retrofit.BaseObserver;
import com.dedpp.dedppmvvm.data.retrofit.RetrofitClient;
import com.dedpp.dedppmvvm.utils.GsonUtil;

import java.util.List;

import io.realm.Realm;

/**
 * SyncService
 * Created by Dedpp on 2017/9/5.
 */

public class SyncService extends IntentService {

    private final static String NAME = "com.ctbaba.cargotracking.service.SyncService";

    public SyncService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final Realm realm = Realm.getDefaultInstance();
        String timestampCarBrand = SPUtils.getInstance(SPKeys.getSP_NAME()).getString(SPKeys.getTIMESTAMP_CAR_BRAND());
        RetrofitClient.getInstance().getBrandList(
                !TextUtils.isEmpty(timestampCarBrand) ? timestampCarBrand : null,
                RetrofitClient.Values.C_TYPE_ANDROID)
                .subscribe(new BaseObserver<List<CarBrandEntity>>() {

                    @Override
                    protected void onHandleSuccess(List<CarBrandEntity> carBrandList) {
                        if (carBrandList.size() > 0) {
                            realm.beginTransaction();
                            for (CarBrandEntity carBrandEntity : carBrandList) {
                                LogUtil.logD("retrofit: " + GsonUtil.toString(carBrandEntity));
                                realm.copyToRealmOrUpdate(carBrandEntity);
                            }
                            realm.commitTransaction();
                            SPUtils.getInstance(SPKeys.getSP_NAME())
                                    .put(SPKeys.getTIMESTAMP_CAR_BRAND(), "");
                        }
                    }
                });
        realm.close();
    }

    public static void openSyncService(Context context, int type) {
        final Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", type);
        context.startService(intent);
    }

}
