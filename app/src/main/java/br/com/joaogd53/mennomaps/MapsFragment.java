package br.com.joaogd53.mennomaps;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.List;

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
        mClusterManager.setAnimation(true);

        double lat = 0.0, lon = 0.0;

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

        Drawable drawable = getResources().getDrawable(R.drawable.background_circle_transparent);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

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
        mClusterManager.setRenderer(new VillageIconRendered(this.getActivity(), mMap, mClusterManager));

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
}

