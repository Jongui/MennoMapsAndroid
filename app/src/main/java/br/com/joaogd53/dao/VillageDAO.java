package br.com.joaogd53.dao;

import android.graphics.Color;
import android.location.Geocoder;
import android.net.Uri;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import java.util.ArrayList;
import java.util.List;

import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;

/**
 * DAO class for Villages
 */

public class VillageDAO {
    private static VillageDAO instance;
    private List<Village> villages;

    private VillageDAO(){
        this.villages = new ArrayList<>();
    }
    public static VillageDAO getInstance(){
        if (instance == null) instance = new VillageDAO();
        return instance;
    }

    public LatLng averagePosition(){
        LatLng ret = new LatLng(0, 0);
        double lat = 0;
        double lon = 0;
        for(Village village : this.villages){
            LatLng tmp = village.getPosition();
            lat += tmp.latitude;
            lon += tmp.longitude;
        }

        int size = this.villages.size();

        if (size != 0){
            lat = lat / size;
            lon = lon / size;
            ret = new LatLng(lat, lon);
        }

        return ret;
    }

    public Village addVillageFromPlacemarker(KmlPlacemark placemark){
        Village village = new Village();
        village.setColonyGroup(placemark.getProperty("Kolonie"));
        village.setName(placemark.getProperty("name"));
        if(placemark.getGeometry().getGeometryType().equals("Point")) {
            KmlPoint point = (KmlPoint) placemark.getGeometry();
            LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
            village.setPosition(latLng);
        }
        village.setDescription(placemark.getProperty("description"));
//        Uri uri = Uri.parse(placemark.getProperty("Link"));
        Colony colony = Colony.ColonyBuilder.findColonyByName(village.getColonyGroup());
        if (colony != null){
            village.setHueColor(colony.getColor());
        } else {
            village.setHueColor(BitmapDescriptorFactory.HUE_RED);
        }
//        village.setUri(uri);
        this.villages.add(village);

        return village;
    }

    public Village getVillageAtIndex(int index){
        return this.villages.get(index);
    }

}
