package com.dedpp.dedppmvvm.data.retrofit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dedpp.dedppmvvm.base.MyApplication;
import com.dedpp.dedppmvvm.R;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient
 * Created by linzhixin on 2017/8/29.
 */

public class RetrofitClient {

    private static final long TIMEOUT = 10;

    //    private static final String BASE_URL = "http://api.baidu.com/";
    private static final String BASE_URL = "http://101.200.157.103:8898/";
//    public static final String BASE_URL = "http://192.168.200.12:8080/";//白海安

    private static String sCacheDir;

    public static String NET_CACHE = sCacheDir + File.separator + "NetCache";

    public class Values {
        public static final int C_TYPE_ANDROID = 2;
    }

    private static RetrofitApi retrofitApi;

    public static RetrofitApi getInstance() {
        if (retrofitApi == null) {
            instanceApi(MyApplication.getAppContent());
        }
        return retrofitApi;
    }

    private static synchronized void instanceApi(final Context context) {
        if (retrofitApi == null) {
            final String cid = String.valueOf(getIMei(context));
            final String ctype = String.valueOf(Values.C_TYPE_ANDROID);
            final String phoneInfo = getFingerprint();
            final String version = String.valueOf(getVersion(context));
            final String ip = String.valueOf(getHostIP());
            final Interceptor headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("cid", cid)
                            .header("ctype", ctype)
                            .header("phone_info", phoneInfo)
                            .header("version", version)
                            .header("ip", ip)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            };
            /*
             * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
             */
            Interceptor cacheInterceptor = null;
            Cache cache = null;
            Context appContext = MyApplication.getAppContent();
            if (appContext != null && TextUtils.isEmpty(sCacheDir)) {
                if (appContext.getExternalCacheDir() != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    sCacheDir = appContext.getExternalCacheDir().toString();
                } else {
                    sCacheDir = appContext.getCacheDir().toString();
                }
                // 缓存 http://www.jianshu.com/p/93153b34310e
                File cacheFile = new File(NET_CACHE);
                cache = new Cache(cacheFile, 1024 * 1024 * 50);
                cacheInterceptor = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        if (!NetworkUtils.isConnected()) {
                            request = request.newBuilder()
                                    .cacheControl(CacheControl.FORCE_CACHE)
                                    .build();
                        }
                        Response response = chain.proceed(request);
                        Response.Builder newBuilder = response.newBuilder();
                        if (NetworkUtils.isConnected()) {
                            int maxAge = 0;
                            // 有网络时 设置缓存超时时间0个小时
                            newBuilder.header("Cache-Control", "public, max-age=" + maxAge);
                        } else {
                            // 无网络时，设置超时为4周
                            int maxStale = 60 * 60 * 24 * 28;
                            newBuilder.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
                        }
                        return newBuilder.build();
                    }
                };
            }
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .cache(cache)
//                    .addInterceptor(cacheInterceptor)
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    //错误重连
                    .retryOnConnectionFailure(true)
                    .cookieJar(new JavaNetCookieJar(cookieManager))
//                    .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context)))
                    .addInterceptor(headerInterceptor)
                    .build();

            retrofitApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
                    .create(RetrofitApi.class);
        }
    }

    /**
     * 线程调度
     */
    public static <T> ObservableTransformer<T, T> compose(
            final LifecycleTransformer<T> lifecycle) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                // 可添加网络连接判断等
                                if (!NetworkUtils.isConnected()) {
                                    ToastUtils.showShort(R.string.toast_network_error);
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(lifecycle);
            }
        };
    }

    private static String getIMei(Context context) {
        String iMei = "";
        try {
            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyMgr != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return iMei;
                }
                iMei = telephonyMgr.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iMei;
    }

    private static String getFingerprint() {
        String fingerprint = "";
        try {
            fingerprint = EncodeUtils.base64Encode2String(Build.FINGERPRINT.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(fingerprint))
            fingerprint = "";
        return fingerprint;
    }

    private static String getVersion(Context context) {
        String info = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if (pi != null) {
                int versionCode = pi.versionCode;
                String versionName = pi.versionName;
                info = versionCode + "|" + versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    private static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia != null) {
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        if (!"127.0.0.1".equals(ip)) {
                            hostIp = ia.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(hostIp))
            hostIp = "";
        return hostIp;
    }

}
