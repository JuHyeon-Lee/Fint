package com.example.leon6.fint;

import java.io.Serializable;

public class MissionInfo implements Serializable{

    double lat;
    double lon;
    String hint;
    String id;

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLon() {
        return lon;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public String getId() {
        return id;
    }
}
