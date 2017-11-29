package br.com.joaogd53.mennomaps;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import br.com.joaogd53.dao.ColonyDAO;
import br.com.joaogd53.dao.VillageDAO;
import br.com.joaogd53.model.AppDatabase;
import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;

/**
 * Map fragment
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<Village> mClusterManager;
    private MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps,
                container, false);

        mMapView = rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);



        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        this.mClusterManager = new ClusterManager<>(this.getActivity(), mMap);
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context context = MapsFragment.this.getActivity();
                LinearLayout info = new LinearLayout(MapsFragment.this.getActivity());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                String snippetString = marker.getSnippet();
                snippet.setText(Html.fromHtml(snippetString));

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        mClusterManager.setAnimation(true);

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
//        this.addMarkersFromKml();
        try {
            Village village = Village.getVillages().get(0);

            if (Village.getVillages().size() == 0 || village.getCountry() == null) {
                this.addMarkersFromKml();
            } else {
                this.addMarkersFromSQLite();
            }
        } catch(Exception ex){
            this.addMarkersFromKml();
        }
        mClusterManager.setRenderer(new VillageIconRendered(this.getActivity(), mMap, mClusterManager));

    }

    private void addMarkersFromKml() {
        KmlLayer kmlLayer;
        double lat = 0.0, lon = 0.0;
        int totalVillages = 0;
        try {
            kmlLayer = new KmlLayer(mMap, R.raw.file, this.getActivity());
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
                        Village v = Village.VillageBuilder.addVillageFromPlacemarker(placemark);
                        LatLng tmp = v.getPosition();
                        lat += tmp.latitude;
                        lon += tmp.longitude;
                        LatLng l = v.getPosition();
                        mClusterManager.addItem(v);
                        totalVillages++;
                    }
                }

            }
            if (totalVillages != 0){
                lat /= totalVillages;
                lon /= totalVillages;
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new DataBaseAsyncTask(this.getActivity()).execute();
    }

    private void addMarkersFromSQLite() {
        Drawable drawable = getResources().getDrawable(R.drawable.background_circle_transparent);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        double lat = 0.0, lon = 0.0;

        List<Village> villages = Village.getVillages();
        for(Village village : villages){
            LatLng l = village.getPosition();
            mClusterManager.addItem(village);
            lat += l.latitude;
            lon += l.longitude;

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(village.getLatitude(), village.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        }

        int size = villages.size();
        if (size != 0){
            lat /= size;
            lon /= size;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));


    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private static class DataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

        private ColonyDAO mColonyDAO;
        private VillageDAO mVillageDAO;
        private AppDatabase mAppDatabase;

        private DataBaseAsyncTask(Context context){
            mAppDatabase = Room.databaseBuilder(context, AppDatabase.class, "mennomaps-database.db").build();
            mColonyDAO = mAppDatabase.colonyDAO();
            mVillageDAO = mAppDatabase.villageDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<Village> villages = Village.getVillages();
            Village[] vils = new Village[villages.size()];
            int i = 0;
            for(Village village : villages){
                vils[i] = village;
                i++;
            }
            i = 0;
            List<Colony> colonies = Colony.getColonies();
            Colony[] cols = new Colony[colonies.size()];
            for(Colony colony : colonies){
                cols[i] = colony;
                i++;
            }

            mColonyDAO.insertColonies(cols);
            mVillageDAO.insertVillages(vils);
            return null;
        }
    }

}

