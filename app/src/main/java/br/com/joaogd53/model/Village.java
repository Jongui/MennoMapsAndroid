package br.com.joaogd53.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for Villages
 */

@Entity(foreignKeys = @ForeignKey(entity = Colony.class,
        parentColumns = "idColony",
        childColumns = "idColony"))
public class Village implements ClusterItem{
    @PrimaryKey
    private int idVillage;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "colonyGroup")
    private String colonyGroup;
    @ColumnInfo(name = "district")
    private String district;
    @Ignore
    private Uri uri;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "hueColor")
    private float hueColor;
    @ColumnInfo(name = "idColony")
    private int idColony;
    @Ignore
    private static int currentId = 0;


    @Ignore
    private static List<Village> villages;


    public int getIdVillage() {
        return idVillage;
    }

    public void setIdVillage(int idVillage) {
        this.idVillage = idVillage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColonyGroup() {
        return colonyGroup;
    }

    public void setColonyGroup(String colonyGroup) {
        this.colonyGroup = colonyGroup;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public float getHueColor() {
        return hueColor;
    }

    public void setHueColor(float hueColor) {
        this.hueColor = hueColor;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static List<Village> getVillages() {
        if (villages == null) villages = new ArrayList<>();
        return villages;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(this.latitude, this.longitude);
    }

    @Override
    public String getTitle() {
        return this.name;
    }

    @Override
    public String getSnippet() {
        return this.description;
    }

    public int getIdColony() {
        return idColony;
    }

    public void setIdColony(int idColony) {
        this.idColony = idColony;
    }

    public static Village getVillageAtIndex(int index){
        return villages.get(index);
    }

    public static int getCurrentId() {
        currentId++;
        return currentId;
    }

    public static class VillageBuilder{
        public static Village addVillageFromPlacemarker(KmlPlacemark placemark){
            Village village = new Village();
            village.idVillage = getCurrentId();
            village.setColonyGroup(placemark.getProperty("Kolonie"));
            village.setName(placemark.getProperty("name"));
            if(placemark.getGeometry().getGeometryType().equals("Point")) {
                KmlPoint point = (KmlPoint) placemark.getGeometry();
                LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                village.setLatitude(latLng.latitude);
                village.longitude = latLng.longitude;
            }
            village.setDescription(placemark.getProperty("description"));
//        Uri uri = Uri.parse(placemark.getProperty("Link"));
            Colony colony = Colony.ColonyBuilder.findColonyByName(village.getColonyGroup());
            if (colony != null){
                village.setHueColor(colony.getColor());
                village.idColony = colony.getIdColony();
            } else {
                village.setHueColor(BitmapDescriptorFactory.HUE_RED);
            }
//        village.setUri(uri);
            if (villages == null) villages = new ArrayList<>();
            villages.add(village);

            return village;
        }

        public static void buildFromArray(Village[] vils) {
            List<Village> villages = Village.getVillages();
            for(Village village : vils){
                villages.add(village);
            }
        }
    }

}
