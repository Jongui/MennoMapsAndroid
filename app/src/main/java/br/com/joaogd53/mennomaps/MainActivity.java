package br.com.joaogd53.mennomaps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.maps.android.data.kml.KmlPolygon;
import com.google.maps.android.ui.IconGenerator;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import br.com.joaogd53.dao.VillageDAO;
import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private int currentFragment = 1;
    private GoogleMap mMap;

    // Declare a variable for the cluster manager.
    private ClusterManager<Village> mClusterManager;
    //private HashMap<String, ClusterManager<Village>> mHashClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setUpMap();
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //this.mHashClusterManager = new HashMap<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        this.mClusterManager = new ClusterManager<>(this, mMap);
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setAnimation(true);

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        KmlLayer kmlLayer;
        double lat = 0.0, lon = 0.0;
        int totalVillages = 0;
        try {
            kmlLayer = new KmlLayer(mMap, R.raw.mennoniten_in_brasilien, getApplicationContext());
            kmlLayer.addLayerToMap();
            //Retrieve the first container in the KML layer
            KmlContainer container = kmlLayer.getContainers().iterator().next();
            for (KmlContainer cont : container.getContainers()){
                for (KmlPlacemark placemark : cont.getPlacemarks()) {
                    //Log.e("StyleId: ",placemark.getStyleId());
                    if(placemark.getGeometry() == null){
                        Log.e("Vill. w.o. coordinate: ", placemark.getProperty("name"));
                        continue;
                    }
                    if (placemark.getGeometry() instanceof KmlPoint) {
                        Village v = VillageDAO.getInstance().addVillageFromPlacemarker(placemark);
                        LatLng tmp = v.getPosition();
                        lat += tmp.latitude;
                        lon += tmp.longitude;
                        Geocoder geocoder = new Geocoder(this);
                        LatLng l = v.getPosition();
                        mClusterManager.addItem(v);
                        totalVillages++;
                    } else if (placemark.getGeometry() instanceof KmlPolygon) {
                        Colony.ColonyBuilder.buildFromPlaceMarker(placemark);
                    }
                }

            }
            if (totalVillages != 0){
                lat /= totalVillages;
                lon /= totalVillages;
            }
            mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class OwnIconRendered extends DefaultClusterRenderer<Village> {

        private final IconGenerator mClusterIconGenerator;

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<Village> clusterManager) {
            super(context, map, clusterManager);
            mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
        }

        @Override
        protected void onBeforeClusterItemRendered(Village item, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(item.getHueColor()));
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Village> cluster,
                                               MarkerOptions markerOptions) {
            List<String> countryNames = this.findCountiesNames(cluster);
            StringBuilder sb = new StringBuilder();
            for (String country : countryNames){
                sb.append(country);
                sb.append(", ");
                //title += country + "\t";
            }
            String title = sb.toString();

            markerOptions.title(title);

            List<String> coloniesNames = this.findColoniesNames(cluster);
            sb = new StringBuilder();
            sb.append("Colonies: ");
            for(String colonyName : coloniesNames){
                sb.append(colonyName);
                sb.append(", ");
            }
            String snippet = sb.toString();
            markerOptions.snippet(snippet);
            float avg, total = 0f;

            for (Village v : cluster.getItems()) {
                total += v.getHueColor();
            }

            avg = total / cluster.getItems().size();

            GradientDrawable d = (GradientDrawable) getResources().getDrawable(R.drawable.background_circle);
            d.setColor(ColorUtils.HSLToColor(new float[]{avg, 0.5f, 0.5f}));
            mClusterIconGenerator.setBackground(d);
            try {
                mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
            } catch (Exception ex){
                ex.getStackTrace();
            }
            final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        }

        private List<String> findCountiesNames(Cluster<Village> cluster) {
            List<String> ret = new ArrayList<>();
            HashMap<String, String> countriesHash = new HashMap<>();
            for (Village v : cluster.getItems()) {
                Colony colony = Colony.ColonyBuilder.findColonyByName(v.getColonyGroup());
                try {
                    String dummy = countriesHash.get(colony.getCountry());
                    if (dummy == null) {
                        countriesHash.put(colony.getCountry(), colony.getName());
                        Locale locale = new Locale("", colony.getCountry());
                        ret.add(locale.getDisplayCountry());
                    }
                } catch (NullPointerException ex){
                    ex.getStackTrace();
                }
            }

            return ret;
        }

        private List<String> findColoniesNames(Cluster<Village> cluster) {
            List<String> ret = new ArrayList<>();
            HashMap<String, String> colonyHash = new HashMap<>();
            for(Village v : cluster.getItems()){
                String colonyGroup = colonyHash.get(v.getColonyGroup());
                if (colonyGroup == null){
                    colonyHash.put(v.getColonyGroup(), v.getName());
                    ret.add(v.getColonyGroup());
                }
            }
            return ret;
        }


    }

}
