package com.dedpp.dedppmvvm.act;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dedpp.dedppmvvm.R;
import com.dedpp.dedppmvvm.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public Presenter presenter;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(); //初始化DataBinding
    }

    private void inject() {
        //现在我们通过DataBindingUtil设置布局文件
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //初始化Presenter对象
        presenter = new Presenter();
        //将presenter对象赋予XML中的 data -> variable -> presenter
        binding.setPresenter(presenter);
    }

    public class Presenter {

        public final ObservableField<String> message = new ObservableField<>();

        public void setMessage(String message) {
            this.message.set(message);
        }

        public void baseDataBinding() {
            Log.d("dedpp", "aaaaaaaaaaaaaaa");
            setMessage("dedpp");
        }
    }
}
