package com.example.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String temperature, icon, city, weatherType ;
    private int condition ;

    public static WeatherData fromJson(JSONObject jsonObject){

        try{
            WeatherData weatherData = new WeatherData() ;
            weatherData.city = jsonObject.getString("name") ;
            weatherData.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id") ;
            weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main") ;
            weatherData.icon = updateWeatherIcon(weatherData.condition) ;
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15 ;
            int roundedValue = (int) Math.rint(tempResult) ;
            weatherData.temperature = Integer.toString(roundedValue) ;
            return weatherData ;
        }
         catch (JSONException e) {
            e.printStackTrace();
            return null ;
        }
    }

    private static String updateWeatherIcon(int cond){
        if(cond >=0 && cond <300){
            return "thunderstorm1" ;
        }

        else if(cond >=300 && cond <500){
            return "lightrain" ;
        }
        else if(cond >=500 && cond <600){
            return "shower" ;
        }
        else if(cond >=600 && cond <700){
            return "snow2" ;
        }
        else if(cond >=700 && cond <771){
            return "fog" ;
        }
        else if(cond >=771 && cond <800){
            return "overcast" ;
        }
        else if(cond == 800){
            return "sunny" ;
        }
        else if(cond >= 801 && cond <804) {
            return "cloudy";
        }
        else if(cond >=900 && cond <902){
            return "thunderstorm1" ;
        }
        else if(cond == 903){
            return "snow1" ;
        }

        else if(cond == 904){
            return "sunny" ;
        }

        else if(cond >=905 && cond <1000){
            return "thunderstorm2" ;
        }

        return "cloudy" ;
    }

    public String getTemperature() {
        return temperature + "°C";
    }

    public String getIcon() {
        return icon;
    }

    public String getCity() {
        return city;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
