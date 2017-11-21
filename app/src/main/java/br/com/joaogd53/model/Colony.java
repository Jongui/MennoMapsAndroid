package br.com.joaogd53.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.maps.android.data.kml.KmlPlacemark;

import java.util.HashMap;

@Entity
public class Colony {
    @PrimaryKey
    private int idColony;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "color")
    private float color;
    @ColumnInfo(name = "country")
    private String country;
    @Ignore
    private static HashMap<String, Colony> colonyList;

    public Colony() {
        if (colonyList == null) {
            colonyList = new HashMap<>();
        }

    }

    public int getIdColony() {
        return idColony;
    }

    public void setIdColony(int idColony) {
        this.idColony = idColony;
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
