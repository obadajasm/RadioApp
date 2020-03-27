package com.obadajasem.blablabla.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("stationcount")
    @Expose
    private Integer stationcount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStationcount() {
        return stationcount;
    }

    public void setStationcount(Integer stationcount) {
        this.stationcount = stationcount;
    }

}