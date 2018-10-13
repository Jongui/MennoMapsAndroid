package br.com.joaogd53.mennomaps;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
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

import java.util.ArrayList;
import java.util.List;

import br.com.joaogd53.dao.ColonyDAO;
import br.com.joaogd53.dao.Migration1To2;
import br.com.joaogd53.dao.Migration2To3;
import br.com.joaogd53.dao.Migration3To4;
import br.com.joaogd53.dao.Migration4To5;
import br.com.joaogd53.dao.VillageDAO;
import br.com.joaogd53.model.AppDatabase;
import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;
import br.com.joaogd53.utils.NetworkUtils;

/**
 * Map fragment
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ClusterManager<Village> mClusterManager;
    private MapView mMapView;
    private int mSelectedVillage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps,
                container, false);

        mMapView = rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        if(this.getActivity() != null) {
            AppDatabase appDatabase = Room.databaseBuilder(this.getActivity(), AppDatabase.class, "mennomaps-database.db")
                    .addMigrations(new Migration1To2(1, 2), new Migration2To3(2, 3), new Migration3To4(3, 4),
                            new Migration4To5(4, 5)).build();
            ColonyDAO colonyDAO = appDatabase.colonyDAO();
            VillageDAO villageDAO = appDatabase.villageDAO();
            if (NetworkUtils.networkIsConnected(this.getActivity())) {
                new UpdateSQLiteAsyncTask(colonyDAO, villageDAO).execute();
            }
        }
        this.mSelectedVillage = -1;
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(this.getActivity() != null)
            this.mClusterManager = new ClusterManager<>(this.getActivity(), mMap);
        else
            return;
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
                String titleText = marker.getTitle();
                try {
                    String[] titleArray = titleText.split("/");
                    title.setText(titleArray[1]);
                    if (MapsFragment.this.getActivity() != null && NetworkUtils.networkIsConnected(MapsFragment.this.getActivity())){
                        mSelectedVillage = Integer.parseInt(titleArray[0]);
                    } else {
                        mSelectedVillage = Integer.parseInt(titleArray[2]);
                    }
                } catch (ArrayIndexOutOfBoundsException ex){
                    title.setText(titleText);
                }
                TextView snippet = new TextView(context);
                String snippetString = marker.getSnippet();
                try {
                    snippet.setText(Html.fromHtml(this.formatSnippet(snippetString)));
                } catch (ArrayIndexOutOfBoundsException ex){
                    snippet.setText(snippetString);
                } catch (NullPointerException ex) {
                    snippet.setText("");
                }
                info.addView(title);
                info.addView(snippet);

                return info;
            }

            private String formatSnippet(String snippetString) throws ArrayIndexOutOfBoundsException{
                String ret;
                String[] snippetArray = snippetString.split("/");
                ret = "<b>" + getResources().getString(R.string.nr) + "</b>" + snippetArray[0] +
                        "<br><b>" + getResources().getString(R.string.country) +"</b>: " + snippetArray[1] +
                        "<br><b>" + getResources().getString(R.string.source) + "</b>: " + snippetArray[2];
                return ret;
            }
        });
        mClusterManager.setAnimation(true);

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        this.addMarkersFromMemory();
        mClusterManager.setRenderer(new VillageIconRendered(this.getActivity(), mMap, mClusterManager));
        mMap.setOnInfoWindowClickListener(this);

    }

    private void addMarkersFromMemory() {
        Drawable drawable = getResources().getDrawable(R.drawable.background_circle_transparent);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        double lat = 0.0, lon = 0.0;

        List<Village> villages = Village.getVillages();
        for (Village village : villages) {
            LatLng l = village.getPosition();
            mClusterManager.addItem(village);
            lat += l.latitude;
            lon += l.longitude;

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(village.getLatitude(), village.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        }

        int size = villages.size();
        if (size != 0) {
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(this.mSelectedVillage == -1)
            return;
        Bundle bundle = new Bundle();
        bundle.putInt("idVillage", this.mSelectedVillage);
        FragmentManagement.getInstance().callFragment(FragmentManagement.VILLAGE_FRAGMENT, bundle,
                this.getFragmentManager());
    }

    private static class UpdateSQLiteAsyncTask extends AsyncTask<Void, Void, Void> {

        private ColonyDAO mColonyDAO;
        private VillageDAO mVillageDAO;

        private UpdateSQLiteAsyncTask(ColonyDAO colonyDAO, VillageDAO villageDAO){
            this.mColonyDAO = colonyDAO;
            this.mVillageDAO = villageDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.updInsColonies();
            this.updInsVillages();
            this.deleteVillages();
            this.deleteColonies();
            return null;
        }

        private void deleteColonies() {
            List<Colony> coloniesDelete = new ArrayList<>();
            Colony[] colsSQLite = mColonyDAO.loadAllColonies();
            List<Colony> colsMemory = Colony.getColonies();
            for(Colony colonySQLite : colsSQLite){
                boolean found = false;
                for(Colony colonyMemory : colsMemory){
                    if(colonyMemory.getIdColony() == colonySQLite.getIdColony()){
                        found = true;
                    }
                }
                if(!found) coloniesDelete.add(colonySQLite);
            }
            Colony[] sqlite = new Colony[coloniesDelete.size()];
            for(int i = 0; i < coloniesDelete.size(); i++){
                sqlite[i] = coloniesDelete.get(i);
            }
            mColonyDAO.deleteColonies(sqlite);
        }

        private void deleteVillages(){
            List<Village> villagesDelete = new ArrayList<>();
            Village[] villSQLite = mVillageDAO.loadAllVillages();
            List<Village> colsMemory = Village.getVillages();
            for(Village colonySQLite : villSQLite){
                boolean found = false;
                for(Village colonyMemory : colsMemory){
                    if(colonyMemory.getIdColony() == colonySQLite.getIdColony()) found = true;
                }
                if(!found) villagesDelete.add(colonySQLite);
            }
            Village[] sqlite = new Village[villagesDelete.size()];
            for(int i = 0; i < villagesDelete.size(); i++){
                sqlite[i] = villagesDelete.get(i);
            }
            mVillageDAO.deleteVillages(sqlite);
        }

        private void updInsVillages() {
            List<Village> villages = Village.getVillages();
            Village[] villSQLite = mVillageDAO.loadAllVillages();
            List<Village> villagesUpdate = new ArrayList<>();
            List<Village> villagesInsert = new ArrayList<>();
            for(int i = 0; i < villages.size(); i++){
                Village village = villages.get(i);
                boolean updVil = false;
                for(Village villageSQLite : villSQLite){
                    if(village.getIdVillage() == villageSQLite.getIdVillage()){
                        updVil = true;
                        break;
                    }
                }
                if(updVil){
                    villagesUpdate.add(village);
                } else {
                    villagesInsert.add(village);
                }
            }
            Village[] sqlite = new Village[villagesInsert.size()];
            for(int i = 0; i < villagesInsert.size(); i++){
                sqlite[i] = villagesInsert.get(i);
            }
            mVillageDAO.insertVillages(sqlite);
            sqlite = new Village[villagesUpdate.size()];
            for(int i = 0; i < villagesUpdate.size(); i++){
                sqlite[i] = villagesUpdate.get(i);
            }
            mVillageDAO.updateVillages(sqlite);
        }

        private void updInsColonies() {
            List<Colony> colsMemory = Colony.getColonies();
//            int lastId = mColonyDAO.lastIndex();
            Colony[] colsSQLite = mColonyDAO.loadAllColonies();
            List<Colony> coloniesUpdate = new ArrayList<>();
            List<Colony> coloniesInsert = new ArrayList<>();
            for(int i = 0; i < colsMemory.size(); i++){
                Colony colonyMemory = colsMemory.get(i);
                boolean updCol = false;
                for(Colony colonySQLite : colsSQLite){
                    if(colonyMemory.getIdColony() == colonySQLite.getIdColony()){
                        updCol = true;
                        break;
                    }
                }
                if(updCol){
                    coloniesUpdate.add(colonyMemory);
                } else {
                    coloniesInsert.add(colonyMemory);
                }
            }
            Colony[] sqlite = new Colony[coloniesInsert.size()];
            for(int i = 0; i < coloniesInsert.size(); i++){
                sqlite[i] = coloniesInsert.get(i);
            }
            mColonyDAO.insertColonies(sqlite);
            sqlite = new Colony[coloniesUpdate.size()];
            for(int i = 0; i < coloniesUpdate.size(); i++){
                sqlite[i] = coloniesUpdate.get(i);
            }
            mColonyDAO.updateColonies(sqlite);
        }
    }
}