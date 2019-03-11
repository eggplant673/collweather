package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    private int id;
    private String provincename;
    private int provincecode;

    public int getId() {
        return id;
    }

    public String getProvincename() {
        return provincename;
    }

    public int getProvincecode() {
        return provincecode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public void setProvincecode(int provincecode) {
        this.provincecode = provincecode;
    }
}
