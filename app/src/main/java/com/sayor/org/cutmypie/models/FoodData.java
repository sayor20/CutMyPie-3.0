package com.sayor.org.cutmypie.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "FoodData")
public class FoodData extends Model {

    @Column(name = "fooddesc")
    String fooddesc;

    @Column(name = "feedcap")
    String feedcap;

    @Column(name = "timeexp")
    String timeexp;

    @Column(name = "lat")
    Double lat;

    @Column(name = "lon")
    Double lon;

    @Column(name = "photo")
    byte[] image;

    @Column(name = "ownerid")
    String ownerid;

    @Column(name = "ownername")
    String ownername;

    public byte[] getImage() {
        return image;
    }

    public String getFooddesc() {
        return fooddesc;
    }

    public String getFeedcap() {
        return feedcap;
    }

    public String getTimeexp() {
        return timeexp;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getOwnername() {
        return ownername;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public FoodData(String fooddesc, String feedcap, String timeexp, Double lat, Double lon, byte[] imgfile, String ownerid, String ownername) {
        this.fooddesc = fooddesc;
        this.feedcap = feedcap;
        this.timeexp = timeexp;
        this.lat = lat;
        this.lon = lon;
        this.image = imgfile;
        this.ownerid = ownerid;
        this.ownername = ownername;
    }

    public FoodData() {
        super();
    }
}


/*
package com.sayor.org.cutmypie;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("FoodData")
public class FoodData extends ParseObject{

    String fooddesc;
    String feedcap;
    String timeexp;
    Number lat;
    Number lon;

    public FoodData(){
        super();
    }

    public String getFooddesc() {
        return getString(fooddesc);
    }

    public void setFooddesc(String fooddesc) {
        put("fooddesc", fooddesc);
    }

    public String getFeedcap() {
        return getString(feedcap);
    }

    public void setFeedcap(String feedcap) {
        put("feedcap", feedcap);
    }

    public String getTimeexp() {
        return getString(timeexp);
    }

    public void setTimeexp(String timeexp) {
        put("timeexp", timeexp);
    }

    public Number getLat() {return getNumber(String.valueOf(lat));
    }

    public void setLat(double lat) {
        put("lat", lat);
    }

    public Number getLon() {return getNumber(String.valueOf(lon));
    }

    public void setLon(double lon) {
        put("lon", lon);
    }

    public String toString() {
        return "fooddesc = " + fooddesc + ", feedcap = " + feedcap + ", timeexp = " + timeexp + ", lat = " + lat + ", lon = " + lon;
    }
}

*/