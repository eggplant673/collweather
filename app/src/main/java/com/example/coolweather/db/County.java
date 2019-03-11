package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    private int id;
    private String countyname;
    private int cityid;
    private String weatherid;

    public int getId() {
        return id;
    }

    public String getCountyname() {
        return countyname;
    }

    public int getCityid() {
        return cityid;
    }

    public String getWeatherid() {
        return weatherid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyname(String countyname) {
        this.countyname = countyname;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public void setWeatherid(String weatherid) {
        this.weatherid = weatherid;
    }
}
