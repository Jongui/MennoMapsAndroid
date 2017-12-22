package br.com.joaogd53.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Model class for Villages
 */

@Entity(foreignKeys = @ForeignKey(entity = Colony.class,
        parentColumns = "idColony",
        childColumns = "idColony"),
        indices = {@Index("idColony")})
public class Village implements ClusterItem {
    @PrimaryKey
    private int idVillage;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "colonyGroup")
    private String colonyGroup;
    @ColumnInfo(name = "district")
    private String district;
    @ColumnInfo(name = "country")
    private String country;
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
    @ColumnInfo(name = "source")
    private String source;
    @ColumnInfo(name = "firebaseKey")
    private int firebaseKey;
    @Ignore
    private DatabaseReference databaseReference;

    @Ignore
    private final static String REFERENCE_NAME = "prd/Village/";

    private static int currentId = 0;

    @Ignore
    private static SparseArray<Village> villages;


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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(int firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public static List<Village> getVillages() {
        if (villages == null) villages = new SparseArray<>();
        List<Village> ret = new ArrayList<>(villages.size());
        for(int i = 0; i < villages.size(); i++){
            ret.add(villages.valueAt(i));
        }
        return ret;
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
        String countryName;
        try {
            countryName = new Locale("", this.country).getDisplayCountry();
        } catch (NullPointerException ex) {
            countryName = new Locale("", "RU").getDisplayName();
        }

        String ret = "<b>Nr.:</b>" + idVillage +
                "<br><b>Kolonie</b>: " + colonyGroup +
                "<br><b>Country</b>: " + countryName +
                "<br><b>Latitude</b>: " + latitude +
                "<br><b>Longitude</b>: " + longitude +
                "<br><b>Source</b>: " + source;
        Log.d(this.name, ret);
        return ret;
    }

    public int getIdColony() {
        return idColony;
    }

    public void setIdColony(int idColony) {
        this.idColony = idColony;
    }

    public static Village getVillageAtIndex(int index) {
        return villages.get(index);
    }

    public static int getCurrentId() {
        currentId++;
        return currentId;
    }


    private static SparseArray<Village> initVillages() {
        villages = new SparseArray<>();
        return villages;
    }

    public static class VillageBuilder {
        public static Village addVillageFromPlacemarker(KmlPlacemark placemark) {
            Village village = new Village();
            village.idVillage = getCurrentId();
            village.setColonyGroup(placemark.getProperty("Kolonie"));
            village.setName(placemark.getProperty("name"));
            village.setCountry(placemark.getProperty("Land"));
            village.setSource(placemark.getProperty("Source"));
            if (placemark.getGeometry().getGeometryType().equals("Point")) {
                KmlPoint point = (KmlPoint) placemark.getGeometry();
                LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                village.setLatitude(latLng.latitude);
                village.longitude = latLng.longitude;
            }
            village.setDescription(placemark.getProperty("description"));
//        Uri uri = Uri.parse(placemark.getProperty("Link"));
            Colony colony = Colony.ColonyBuilder.findColonyByName(village.getColonyGroup());
            if (colony != null) {
                village.setHueColor(colony.getColor());
                village.idColony = colony.getIdColony();
            } else {
                village.setHueColor(BitmapDescriptorFactory.HUE_RED);
            }
//        village.setUri(uri);
            if (villages == null) villages = new SparseArray<>();
            villages.put(village.idVillage, village);
            return village;
        }

        public static void buildFromArray(Village[] vils) {
            SparseArray<Village> villages = Village.initVillages();
            for (Village v : vils) {
                villages.put(v.idVillage, v);
            }
        }

        public static Village buildFromSnapshot(DataSnapshot villageSnapshot) {
            Village ret;
            if (villages == null) villages = new SparseArray<>();
            int id = getCurrentId();
            ret = villages.get(id);
            if (ret == null) {
                ret = new Village();
                ret.idVillage = id;
                villages.put(ret.idVillage, ret);
            }
            ret.firebaseKey = Integer.valueOf(villageSnapshot.getKey());
            ret.name = villageSnapshot.child("Name").getValue().toString();
            ret.colonyGroup = villageSnapshot.child("Kolonie").getValue().toString();
            if(ret.colonyGroup.equals("")){
                ret.colonyGroup = "No name";
            }
            ret.country = villageSnapshot.child("Land").getValue().toString();
            Colony colony = Colony.ColonyBuilder.findColonyByName(ret.colonyGroup);
            if (colony != null) {
                ret.setHueColor(colony.getColor());
                ret.idColony = colony.getIdColony();
            } else {
                ret.setHueColor(BitmapDescriptorFactory.HUE_RED);
            }
            ret.latitude = Double.valueOf(villageSnapshot.child("Latitude").getValue().toString());
            ret.longitude = Double.valueOf(villageSnapshot.child("Longitude").getValue().toString());
            ret.source = villageSnapshot.child("Source").getValue().toString();
            String ref = REFERENCE_NAME + ret.firebaseKey;
            ret.databaseReference = FirebaseDatabase.getInstance().getReference(ref);
            ret.description = ret.getSnippet();
            return ret;
        }
    }


}
