package br.com.joaogd53.mennomaps;

import android.app.FragmentManager;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.joaogd53.dao.ColonyDAO;
import br.com.joaogd53.dao.ColonyFirebaseDAO;
import br.com.joaogd53.dao.FirebaseDAO;
import br.com.joaogd53.dao.Migration1To2;
import br.com.joaogd53.dao.Migration2To3;
import br.com.joaogd53.dao.Migration3To4;
import br.com.joaogd53.dao.Migration4To5;
import br.com.joaogd53.dao.VillageDAO;
import br.com.joaogd53.dao.VillageFirebaseDAO;
import br.com.joaogd53.model.AppDatabase;
import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;
import br.com.joaogd53.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private int currentFragment = 1;
    private AppDatabase appDatabase;
    private ProgressBar progressBar;
    private TextView textView;

    // Declare a variable for the cluster manager.
    //private HashMap<String, ClusterManager<Village>> mHashClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "mennomaps-database.db")
                .addMigrations(new Migration1To2(1, 2), new Migration2To3(2, 3), new Migration3To4(3, 4),
                        new Migration4To5(4, 5)).build();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        FragmentManagement.getInstance().setActivity(this);
    }

    private void processData(){
        if (NetworkUtils.networkIsConnected(this)) {
            this.loadDataOnline();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            new DataBaseAsyncTask(this.getFragmentManager(), appDatabase).execute();
        }
    }

    private void loadDataOnline() {
        textView.setText("Loading colonies");
        ColonyFirebaseDAO.getInstance().addFirebaseDAO(new FirebaseDAO() {
            @Override
            public void atLoadFinished() {
                VillageFirebaseDAO villageFirebaseDAO = VillageFirebaseDAO.getInstance();
                textView.setText("Loading villages");
                villageFirebaseDAO.addFirebaseDAO(new FirebaseDAO() {
                    @Override
                    public void atLoadFinished() {
                        textView.setText("Updating info");
                        ColonyDAO colonyDAO = appDatabase.colonyDAO();
                        VillageDAO villageDAO = appDatabase.villageDAO();
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        FragmentManagement.getInstance().callFragment(FragmentManagement.MAPS_FRAGMENT, null);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.processData();
    }

    private static class DataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

        private FragmentManager mFragmentManager;
        private ColonyDAO mColonyDAO;
        private VillageDAO mVillageDAO;
        private AppDatabase mAppDatabase;

        private DataBaseAsyncTask(FragmentManager fragmentManager, AppDatabase appDatabase) {
            this.mFragmentManager = fragmentManager;
            mAppDatabase = appDatabase;
            mColonyDAO = mAppDatabase.colonyDAO();
            mVillageDAO = mAppDatabase.villageDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Colony[] cols = mColonyDAO.loadAllColonies();
            Village[] vils = mVillageDAO.loadAllVillages();
            Colony.ColonyBuilder.buildFromArray(cols);
            Village.VillageBuilder.buildFromArray(vils);
            FragmentManagement.getInstance().callFragment(FragmentManagement.MAPS_FRAGMENT, null);
            return null;
        }
    }

}