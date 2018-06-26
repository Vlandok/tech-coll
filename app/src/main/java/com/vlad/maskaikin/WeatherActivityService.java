package com.vlad.maskaikin;

import com.vlad.maskaikin.getWeather.ResultWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherActivityService {

    @GET("weather")
    Call<ResultWeather> getDeg(@Query("q") String city, @Query("units") String units, @Query("APPID") String APPID);

}
