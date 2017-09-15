package com.dedpp.dedppmvvm.custom;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dedpp.dedppmvvm.R;

import java.util.List;

/**
 * DataBindingAdapter
 * Created by Dedpp on 2017/9/11.
 */

public abstract class DataBindingAdapter<T, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    public DataBindingAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public DataBindingAdapter(@Nullable List<T> data) {
        super(data);
    }

    public DataBindingAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        if (binding == null)
            return super.getItemView(layoutResId, parent);
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding);
        return view;
    }

}
