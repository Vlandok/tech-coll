package com.vlad.maskaikin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vlad.maskaikin.getCity.*;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends Activity {


    private Retrofit retrofitGetCity;
    private TextView textViewInfo;
    private Button buttonGoOrSettings;
    private String coordinates = "";
    private LocationManager locationManager;
    private SharedPreferences sharedPreferences;
    private String city;

    public static final String MY_SETTINGS = "my_settings";
    public static final String CITY = "getCity";
    public static final String RETROFIT_GET_CITY_BASE_URL = "https://geocode-maps.yandex.ru/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        boolean hasVisited = sharedPreferences.getBoolean("hasVisited", false);
        if (hasVisited && city != "") {
            if (sharedPreferences.contains(CITY)) {
                city = sharedPreferences.getString(CITY, "");
                startActivity(WeatherActivity.createStartIntent(MainActivity.this).putExtra(CITY, city));
                finish();
            }
        }


        textViewInfo = findViewById(R.id.textViewInfo);
        buttonGoOrSettings = findViewById(R.id.buttonGoOrSettings);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofitGetCity = new Retrofit.Builder()
                .baseUrl(RETROFIT_GET_CITY_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textViewInfo.setText(R.string.provider_enabled_false);
            buttonGoOrSettings.setText(R.string.button_settings_phone);
            buttonGoOrSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000 * 10, 10, locationListener);
        checkEnabled();

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasVisited", true);
        editor.apply();

        SharedPreferences.Editor editorCity = sharedPreferences.edit();
        editorCity.putString(CITY, city);
        editorCity.apply();

        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            formatLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            formatLocation(locationManager.getLastKnownLocation(provider));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    @SuppressLint("DefaultLocale")
    private void formatLocation(Location location) {
        if (location == null)
            return;
        textViewInfo.setText(R.string.defining_city);
        double lat = location.getLatitude();
        double log = location.getLongitude();
        coordinates = log + "," + lat;
        getCityRetrofit();
    }

    public void getCityRetrofit() {

        MyResultService service = retrofitGetCity.create(MyResultService.class);

        Call<MyResult> call = service.getData(coordinates, "json");
        call.enqueue(new Callback<MyResult>() {


            @Override
            public void onResponse(Call<MyResult> call, retrofit2.Response<MyResult> response) {
                city = response.body().getResponse().getGeoObjectCollection().getFeatureMember().get(0)
                        .getGeoObject().getMetaDataProperty().getGeocoderMetaData().getAddressDetails()
                        .getCountry().getAdministrativeArea().getSubAdministrativeArea().getLocality().getLocalityName();
                textViewInfo.setText(city);

                buttonGoOrSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(WeatherActivity.createStartIntent(MainActivity.this).putExtra(CITY, city));
                    }
                });
            }

            @Override
            public void onFailure(Call<MyResult> call, Throwable t) {
                textViewInfo.setText(R.string.defining_city_fail);
                buttonGoOrSettings.setText(R.string.defining_city_try_again);
                buttonGoOrSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCityRetrofit();
                    }
                });
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void checkEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            textViewInfo.setText(R.string.provider_enabled_false);
            buttonGoOrSettings.setText(R.string.button_settings_phone);
            buttonGoOrSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });

        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            textViewInfo.setText(R.string.get_coordinates);
            buttonGoOrSettings.setText(R.string.next);
            buttonGoOrSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Ожидаем получение координат.")
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            });


        }
    }


}
