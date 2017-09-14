package com.dedpp.dedppmvvm.data.retrofit;


import com.dedpp.dedppmvvm.data.model.BaseEntity;
import com.dedpp.dedppmvvm.data.model.CarBrandEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * RetrofitApi
 * Created by linzhixin on 2017/9/4.
 */

public interface RetrofitApi {

//    @FormUrlEncoded
//    @POST("account/login")
//    Observable<BaseEntity<UserEntity>> login(
//            @Field("userId") String userId,
//            @Field("password") String password
//    );

    @FormUrlEncoded
    @POST("api/listbrand")
    Observable<BaseEntity<List<CarBrandEntity>>> getBrandList(
            @Field("time_stamp") String timeStamp,
            @Field("ctype") int ctype
    );

}
