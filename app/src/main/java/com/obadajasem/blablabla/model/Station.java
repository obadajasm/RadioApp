package com.obadajasem.blablabla.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Station implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("changeuuid")
    @Expose
    private String changeuuid;
    @SerializedName("stationuuid")
    @Expose
    private String stationuuid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("favicon")
    @Expose
    private String favicon;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("countrycode")
    @Expose
    private String countrycode;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("votes")
    @Expose
    private String votes;
    @SerializedName("negativevotes")
    @Expose
    private String negativevotes;
    @SerializedName("lastchangetime")
    @Expose
    private String lastchangetime;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("codec")
    @Expose
    private String codec;
    @SerializedName("bitrate")
    @Expose
    private String bitrate;
    @SerializedName("hls")
    @Expose
    private String hls;
    @SerializedName("lastcheckok")
    @Expose
    private String lastcheckok;
    @SerializedName("lastchecktime")
    @Expose
    private String lastchecktime;
    @SerializedName("lastcheckoktime")
    @Expose
    private String lastcheckoktime;
    @SerializedName("clicktimestamp")
    @Expose
    private String clicktimestamp;
    @SerializedName("clickcount")
    @Expose
    private String clickcount;
    @SerializedName("clicktrend")
    @Expose
    private String clicktrend;

    public Station() {

    }

    protected Station(Parcel in) {
        id = in.readString();
        changeuuid = in.readString();
        stationuuid = in.readString();
        name = in.readString();
        url = in.readString();
        homepage = in.readString();
        favicon = in.readString();
        tags = in.readString();
        country = in.readString();
        countrycode = in.readString();
        state = in.readString();
        language = in.readString();
        votes = in.readString();
        negativevotes = in.readString();
        lastchangetime = in.readString();
        ip = in.readString();
        codec = in.readString();
        bitrate = in.readString();
        hls = in.readString();
        lastcheckok = in.readString();
        lastchecktime = in.readString();
        lastcheckoktime = in.readString();
        clicktimestamp = in.readString();
        clickcount = in.readString();
        clicktrend = in.readString();
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChangeuuid() {
        return changeuuid;
    }

    public void setChangeuuid(String changeuuid) {
        this.changeuuid = changeuuid;
    }

    public String getStationuuid() {
        return stationuuid;
    }

    public void setStationuuid(String stationuuid) {
        this.stationuuid = stationuuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getNegativevotes() {
        return negativevotes;
    }

    public void setNegativevotes(String negativevotes) {
        this.negativevotes = negativevotes;
    }

    public String getLastchangetime() {
        return lastchangetime;
    }

    public void setLastchangetime(String lastchangetime) {
        this.lastchangetime = lastchangetime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

    public String getLastcheckok() {
        return lastcheckok;
    }

    public void setLastcheckok(String lastcheckok) {
        this.lastcheckok = lastcheckok;
    }

    public String getLastchecktime() {
        return lastchecktime;
    }

    public void setLastchecktime(String lastchecktime) {
        this.lastchecktime = lastchecktime;
    }

    public String getLastcheckoktime() {
        return lastcheckoktime;
    }

    public void setLastcheckoktime(String lastcheckoktime) {
        this.lastcheckoktime = lastcheckoktime;
    }

    public String getClicktimestamp() {
        return clicktimestamp;
    }

    public void setClicktimestamp(String clicktimestamp) {
        this.clicktimestamp = clicktimestamp;
    }

    public String getClickcount() {
        return clickcount;
    }

    public void setClickcount(String clickcount) {
        this.clickcount = clickcount;
    }

    public String getClicktrend() {
        return clicktrend;
    }

    public void setClicktrend(String clicktrend) {
        this.clicktrend = clicktrend;
    }


}
