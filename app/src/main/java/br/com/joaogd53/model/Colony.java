package br.com.joaogd53.model;


import com.google.maps.android.data.kml.KmlPlacemark;

import java.util.HashMap;

public class Colony {
    private String name;
    private float color;
    private String country;
    private static HashMap<String, Colony> colonyList;

    private Colony() {
        if (colonyList == null) {
            colonyList = new HashMap<>();
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getColor() {
        return color;
    }

    public void setColor(float color) {
        this.color = color;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static class ColonyBuilder {
        public static Colony buildFromPlaceMarker(KmlPlacemark placemark) {
            Colony colony = new Colony();
            colony.name = placemark.getProperty("name");
            colony.color = Float.parseFloat(placemark.getProperty("hue_color"));
            colony.country = placemark.getProperty("country");
            colonyList.put(colony.name, colony);
            return colony;
        }

        public static Colony findColonyByName(String name) {
            return colonyList.get(name);
        }

    }

}
