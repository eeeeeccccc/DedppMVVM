package com.dedpp.dedppmvvm.custom;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.dedpp.dedppmvvm.R;

/**
 * DataBindingHolder
 * Created by Dedpp on 2017/9/11.
 */

public class DataBindingHolder extends BaseViewHolder {

    public DataBindingHolder(View view) {
        super(view);
    }

    public ViewDataBinding getBinding() {
        return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
    }

}
