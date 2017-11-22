package br.com.joaogd53.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.kml.KmlPlacemark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private List<LatLng> latLngs;
    @Ignore
    private static float currentColor;
    @Ignore
    private static int currentId = 0;

    @Ignore
    private static HashMap<String, Colony> colonyList;


    public Colony() {
        if (colonyList == null) {
            colonyList = new HashMap<>();
        }
        latLngs = new ArrayList<>();
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

    public List<LatLng> getLatLngs() {
        if (latLngs == null) latLngs = new ArrayList<>();
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public static void setColonyList(HashMap<String, Colony> colonyList) {
        Colony.colonyList = colonyList;
    }

    public static HashMap<String, Colony> getColonyList() {
        if(colonyList == null) colonyList = new HashMap<>();
        return colonyList;
    }

    public static float getCurrentColor() {
        currentColor += 31;
        if (currentColor > 360){
            currentColor -= 360;
        }
        return currentColor;
    }

    public static int getCurrentId() {
        currentId++;
        return currentId;
    }

    public static List<Colony> getColonies(){
        List<Colony> ret = new ArrayList<>();
        for(Colony colony : colonyList.values()){
            ret.add(colony);
        }
        return ret;
    }

    public static class ColonyBuilder {
        public static Colony buildFromPlaceMarker(KmlPlacemark placemark) {
            Colony colony = new Colony();
            colony.name = placemark.getProperty("name");
            colony.color = Colony.getCurrentColor();
            colony.country = placemark.getProperty("country");
            colonyList.put(colony.name, colony);
            return colony;
        }

        public static Colony findColonyByName(String name) {
            Colony ret = colonyList.get(name);
            if (ret == null){
                ret = buildFromColonyName(name);
            }
            return ret;
        }

        public static void buildFromArray(Colony[] cols) {
            HashMap<String, Colony> hashMap = Colony.getColonyList();
            for(Colony colony : cols){
                hashMap.put(colony.name, colony);
            }
        }

        public static Colony buildFromColonyName(String colonyGroup) {
            Colony ret = new Colony();
            ret.idColony = Colony.getCurrentId();
            ret.color = Colony.getCurrentColor();
            ret.country = "RU";
            ret.name = colonyGroup;
            colonyList.put(colonyGroup, ret);
            return ret;
        }
    }

}
