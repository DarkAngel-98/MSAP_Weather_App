package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity{


    final String APP_ID = "e2ee357aa36eb369ebb03321e0f87896";
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather" ;


    final long MIN_TIME = 10000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String Location_Provider = LocationManager.NETWORK_PROVIDER;

    TextView temperature, weatherCondition, cityName;
    ImageView weatherIcon;
    RelativeLayout relativeLayout;

    Intent intent;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = findViewById(R.id.temperature);
        weatherCondition = findViewById(R.id.weatherCondition);
        cityName = findViewById(R.id.cityName);
        weatherIcon = findViewById(R.id.weatherIcon);

        //getWeatherForCurrentLocation();

        relativeLayout = (RelativeLayout) findViewById(R.id.cityFinder);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, CityFinder.class);
                startActivity(intent);
            }
        });
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }
     */


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent() ;
        String city = intent.getStringExtra("city") ;

        if(city != null){
            getWeatherForNewCity(city);
        }
        else{
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewCity(String city){
        RequestParams params = new RequestParams() ;
        params.put("q", city);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);
    }

    private void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams() ;
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params) ;

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 /* && grantResults[0] == PackageManager.PERMISSION_GRANTED */){
                Toast.makeText(MainActivity.this, "Location get successfully", Toast.LENGTH_SHORT).show() ;
                getWeatherForCurrentLocation();
            }
            else{
                Toast.makeText(MainActivity.this, "User denied the permission", Toast.LENGTH_SHORT).show() ;
            }
        }
    }

    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient() ;
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(MainActivity.this, "Data get success", Toast.LENGTH_SHORT).show();

                WeatherData weatherData = WeatherData.fromJson(response) ;
                UpdateUI(weatherData) ;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        }) ;
    }

    private void UpdateUI(WeatherData weatherData){
        temperature.setText(weatherData.getTemperature());
        cityName.setText(weatherData.getCity());
        weatherCondition.setText(weatherData.getWeatherType());
        int resourceID = getResources().getIdentifier(weatherData.getIcon(), "drawable", getPackageName()) ;
        weatherIcon.setImageResource(resourceID);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}