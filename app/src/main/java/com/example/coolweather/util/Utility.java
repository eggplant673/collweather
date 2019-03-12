package com.example.coolweather.util;

import android.text.TextUtils;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public  static boolean handleProvinceResponse(String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allprovince = new JSONArray(response);
                for(int i=0;i<allprovince.length();i++)
                {
                    JSONObject provinceobject = allprovince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvincename(provinceobject.getString("name"));
                    province.setProvincecode(provinceobject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handlecityresponse(String response,int provinceid){
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allcity = new JSONArray(response);
                for(int i=0;i<allcity.length();i++) {
                    JSONObject cityobject = allcity.getJSONObject(i);
                    City city = new City();
                    city.setCityname(cityobject.getString("name"));
                    city.setCitycode(cityobject.getInt("id"));
                    city.setProvinceid(provinceid);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean handlecountyresponse(String response,int cityid){
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allcounty = new JSONArray(response);
                for(int i=0;i<allcounty.length();i++) {
                    JSONObject countyobject = allcounty.getJSONObject(i);
                    County county = new County();
                   county.setCountyname(countyobject.getString("name"));
                   county.setWeatherid(countyobject.getString("weather_id"));
                   county.setCityid(cityid);
                   county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
