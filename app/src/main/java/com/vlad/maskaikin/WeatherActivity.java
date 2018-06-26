package com.vlad.maskaikin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.vlad.maskaikin.getWeather.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends Activity {

    private TextView textViewCity;
    private TextView textViewDeg;
    private Retrofit retrofitGetWeather;
    private ImageView imageViewIcon;
    private String icon;
    private double tempDouble;
    private String city;
    private Weather weather;
    private Bitmap iconBitmap = null;
    private Realm realm;

    public static final String RETROFIT_GET_WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String GET_ICON_URL = "http://openweathermap.org/img/w/";
    public static final int ICON_WIDTH_HEIGHT = 200;
    public static final int ICON_BITMAP_QUALITY = 100;
    public static final String CELSIUS = "°C";
    public static final String WEATHER_MAPS_APP_ID = "6f60a2e2eba0ac6d5868f11ba9b8c10b";
    public static final String UNITS = "metric";
    public static final String FORMAT_PNG = ".png";
    public static final int ONE_HOUR_INT_MILLISECONDS = 3600000;

    public static Intent createStartIntent(Context context) {

        Intent intent = new Intent(context, WeatherActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Intent intent = getIntent();
        city = (String) intent.getSerializableExtra(MainActivity.CITY);

        textViewCity = findViewById(R.id.textViewCity);
        textViewDeg = findViewById(R.id.textViewDeg);
        imageViewIcon = findViewById(R.id.imageViewIcon);

        textViewCity.setText(city);

        retrofitGetWeather = new Retrofit.Builder()
                .baseUrl(RETROFIT_GET_WEATHER_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        long timeCheckMinusHour = System.currentTimeMillis() - ONE_HOUR_INT_MILLISECONDS;
        RealmResults<Weather> weathers = realm.where(Weather.class).findAll();

        boolean checkTime = false;
        for (int i = 0; i < weathers.size(); i++) {
            if (weathers.get(i).getTime() > timeCheckMinusHour) {
                checkTime = true;
            }
        }

        if (weathers.isEmpty() || !checkTime) {

            WeatherActivityService service = retrofitGetWeather.create(WeatherActivityService.class);

            Call<ResultWeather> call = service.getDeg(city, UNITS, WEATHER_MAPS_APP_ID);
            call.enqueue(new Callback<ResultWeather>() {


                @Override
                public void onResponse(Call<ResultWeather> call, Response<ResultWeather> response) {
                    weather = new Weather();
                    tempDouble = response.body().getMain().getTemp();
                    int tempInteger = (int) tempDouble;
                    textViewDeg.setText(String.valueOf(tempInteger) + CELSIUS);

                    icon = response.body().getWeather().get(0).getIcon();

                    try {
                        URL urlIcon = new URL(GET_ICON_URL + icon + FORMAT_PNG);
                        iconBitmap = BitmapFactory.decodeStream(urlIcon.openConnection().getInputStream());
                        iconBitmap = Bitmap.createScaledBitmap(iconBitmap, ICON_WIDTH_HEIGHT, ICON_WIDTH_HEIGHT, true);

                        imageViewIcon.setImageBitmap(iconBitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    iconBitmap.compress(Bitmap.CompressFormat.PNG, ICON_BITMAP_QUALITY, stream);
                    byte[] byteArray = stream.toByteArray();


                    realm.beginTransaction();
                    weather = realm.createObject(Weather.class);
                    weather.setTemp(tempInteger);
                    weather.setTime(System.currentTimeMillis());
                    weather.setIcon(byteArray);
                    weather.setId(System.currentTimeMillis());
                    realm.copyToRealm(weather);
                    realm.commitTransaction();

                }

                @Override
                public void onFailure(Call<ResultWeather> call, Throwable t) {
                    textViewDeg.setText(R.string.get_temperature_fail);
                }
            });
        } else

        {
            Weather lastWeather = weathers.get(weathers.size() - 1);
            String temp = String.valueOf(lastWeather.getTemp()) + CELSIUS;
            textViewDeg.setText(temp);
            byte[] byteIcon = lastWeather.getIcon();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteIcon, 0, byteIcon.length);
            imageViewIcon.setImageBitmap(bitmap);

        }


    }

}
