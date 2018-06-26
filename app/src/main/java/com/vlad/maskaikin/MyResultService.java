package com.vlad.maskaikin;

import com.vlad.maskaikin.getCity.MyResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyResultService {

    @GET("1.x/")
    Call<MyResult> getData(@Query("geocode") String latitude, @Query("format") String format);

}
