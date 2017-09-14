package com.dedpp.dedppmvvm.act;

import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dedpp.dedppmvvm.BR;
import com.dedpp.dedppmvvm.R;
import com.dedpp.dedppmvvm.base.BaseActivity;
import com.dedpp.dedppmvvm.base.BasePresenter;
import com.dedpp.dedppmvvm.custom.DataBindingAdapter;
import com.dedpp.dedppmvvm.custom.DataBindingHolder;
import com.dedpp.dedppmvvm.data.model.CarBrandEntity;
import com.dedpp.dedppmvvm.data.retrofit.BaseObserver;
import com.dedpp.dedppmvvm.data.retrofit.RetrofitClient;
import com.dedpp.dedppmvvm.databinding.ActivityTestBinding;
import com.dedpp.dedppmvvm.glide.GlideApp;
import com.dedpp.dedppmvvm.utils.LogUtil;
import com.dedpp.dedppmvvm.utils.SPKeys;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * TestActivity
 * Created by linzhixin on 2017/9/6.
 */

public class TestActivity extends BaseActivity<ActivityTestBinding, TestActivity.Presenter> {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_test;
    }

    @Override
    protected void inject() {
        //初始化Presenter对象
        presenter = new Presenter(dataBinding, this);
        //将presenter对象赋予XML中的 data -> variable -> presenter
        presenter.setCount(-1);
        dataBinding.setPresenter(presenter);
    }

    public static class Presenter extends BasePresenter<ActivityTestBinding, TestActivity> {

        public CarBrandAdapter adapter;

        public final ObservableInt count = new ObservableInt();

        public Presenter(ActivityTestBinding dataBinding, TestActivity activity) {
            super(dataBinding, activity);
            realm = Realm.getDefaultInstance();
        }

        @Override
        protected void initView() {
            activity.rxClick(dataBinding.button,
                    o -> dataBinding.getPresenter().refresh());
        }

        public void setCount(int count) {
            this.count.set(count);
        }

        public void refresh() {
            String timestampCarBrand = SPUtils.getInstance(SPKeys.getSP_NAME()).getString(SPKeys.getTIMESTAMP_CAR_BRAND());
            RetrofitClient.getInstance().getBrandList(
                    !TextUtils.isEmpty(timestampCarBrand) ? timestampCarBrand : null,
                    RetrofitClient.Values.C_TYPE_ANDROID)
                    .compose(RetrofitClient.compose(activity.bindToLifecycle()))
                    .subscribe(new BaseObserver<List<CarBrandEntity>>() {

                        @Override
                        protected void onHandleSuccess(List<CarBrandEntity> carBrandList) {
                            if (carBrandList.size() > 0) {
                                realm.beginTransaction();
                                for (CarBrandEntity carBrandEntity : carBrandList) {
                                    realm.copyToRealmOrUpdate(carBrandEntity);
                                }
                                realm.commitTransaction();
                                SPUtils.getInstance(SPKeys.getSP_NAME())
                                        .put(SPKeys.getTIMESTAMP_CAR_BRAND(), "");

                                RealmResults<CarBrandEntity> realmResults = realm.where(CarBrandEntity.class).findAll();
                                setCount(realmResults.size());
                                adapter = new CarBrandAdapter(activity, realmResults);
                                dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                                dataBinding.recyclerView.setAdapter(adapter);
                            }
                        }
                    });
        }

        @Override
        protected void destroy() {
            if (adapter != null) {
                adapter.removeRealmListener();
                adapter = null;
            }
            super.destroy();
        }
    }

    public static class CarBrandAdapter extends DataBindingAdapter<CarBrandEntity, DataBindingHolder> {

        private TestActivity activity;
        private CarBrandPresenter presenter;
        private RealmResults<CarBrandEntity> realmResults;
        private RealmChangeListener<RealmResults<CarBrandEntity>> listener;

        public CarBrandAdapter(TestActivity activity, RealmResults<CarBrandEntity> data) {
            super(R.layout.item_car_brand, data);
            this.activity = activity;
            presenter = new CarBrandPresenter();
            realmResults = data;
            listener = element -> {
                if (realmResults == null || !realmResults.isValid())
                    return;
                notifyDataSetChanged();
            };
            if (realmResults != null) {
                realmResults.addChangeListener(listener);
            }
        }

        public void removeRealmListener() {
            if (realmResults != null && listener != null)
                realmResults.removeChangeListener(listener);
        }

        @Override
        protected void convert(DataBindingHolder helper, CarBrandEntity item) {
            ViewDataBinding binding = helper.getBinding();
            binding.setVariable(BR.carBrand, item);
            binding.setVariable(BR.presenter, presenter);
            binding.executePendingBindings();

            activity.rxClick(helper.getView(R.id.item_brand_item),
                    o -> presenter.carBrand(item));

            ImageView imgBrand = helper.getView(R.id.img_brand);
            GlideApp.with(mContext)
                    .load(item.getImgUrl())
                    .placeholder(R.color.colorAccent)
                    .into(imgBrand);
        }

        public static class CarBrandPresenter {

            public void carBrand(CarBrandEntity carBrand) {
                LogUtil.logE("carBrand : " + carBrand.getName());
                ToastUtils.showShort("carBrand : " + carBrand.getName());
            }
        }
    }

}
